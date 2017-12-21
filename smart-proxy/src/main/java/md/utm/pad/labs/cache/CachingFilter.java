package md.utm.pad.labs.cache;

import org.apache.catalina.ssi.ByteArrayServletOutputStream;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * Created by anrosca on Dec, 2017
 */
@Component
public class CachingFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(CachingFilter.class);

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (request.getMethod().equalsIgnoreCase("GET")) {
            String key = makeKey(request);
            if (cacheManager.containsKey(key)) {
                LOGGER.info("Cache hit. Sending response from Redis.");
                writeResponse(response, cacheManager.getValue(key), cacheManager.getContentTypeFor(key));
            } else {
                LOGGER.info("Cache miss. Sending request to data warehouse and memorizing the response.");
                SimpleHttpServletResponseWrapper responseWrapper = new SimpleHttpServletResponseWrapper((HttpServletResponse) response);
                filterChain.doFilter(request, responseWrapper);
                String responseBody = responseWrapper.getResponseBody();
                String contentType = responseWrapper.getContentType();
                cacheManager.put(key, responseBody, contentType);
                writeResponse(response, responseBody, cacheManager.getContentTypeFor(key));
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String makeKey(HttpServletRequest request) {
        return request.getRequestURI() + "-" + request.getHeader("Accept");
    }

    private void writeResponse(ServletResponse response, String responseBody, String contentType) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try (PrintWriter out = httpResponse.getWriter()) {
            httpResponse.setContentType(contentType);
            out.println(responseBody);
            out.flush();
        } catch (IOException e) {
            LOGGER.error("Error while writing the response body.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
    }

    private static class SimpleHttpServletResponseWrapper extends HttpServletResponseWrapper {
        private ByteArrayServletOutputStream outputStream = new ByteArrayServletOutputStream();

        public SimpleHttpServletResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(outputStream);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return outputStream;
        }

        public String getResponseBody() {
            return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
