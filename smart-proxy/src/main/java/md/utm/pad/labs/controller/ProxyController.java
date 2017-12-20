package md.utm.pad.labs.controller;

import md.utm.pad.labs.exception.UnknownHttpMethodException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by anrosca on Dec, 2017
 */
@RestController
public class ProxyController {
    private static final Logger LOGGER = Logger.getLogger(ProxyController.class);
    private static final Set<String> ignoredHeaders= new HashSet<>();

    static {
        ignoredHeaders.add("host");
    }

    @Autowired
    private List<URI> warehouses;

    @RequestMapping(value = "/**",
            method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.HEAD })
    public ResponseEntity<String> handleRequest(HttpServletRequest request) {
        RestTemplate template = new RestTemplate();
        URI servingNodeUri = warehouses.get(0);
        String requestURI = request.getRequestURI();
        String warehouseUri = servingNodeUri.toString() + requestURI;
        LOGGER.info("Sending the request to: " + warehouseUri);
        return sendRequest(request, warehouseUri, template, request.getMethod().toUpperCase());
    }

    private ResponseEntity<String> sendRequest(HttpServletRequest request, String warehouseUri,
                                               RestTemplate template, String httpMethod) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Forwarded-Host", request.getServerName() + ":" + request.getServerPort());
        copyHeaders(headers, request);
        String body = "";
        switch (httpMethod) {
            case "POST":
            case "PUT":
            case "DELETE":
                body = readBody(request);
            case "GET":
            case "HEAD":
                HttpEntity<String> entity = new HttpEntity<>(body, headers);
                return template.exchange(warehouseUri, HttpMethod.valueOf(httpMethod), entity, String.class);
        }
        throw new UnknownHttpMethodException("Unknown method: " + httpMethod);
    }

    private void copyHeaders(HttpHeaders headers, HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!ignoredHeaders.contains(headerName.toLowerCase()))
                headers.set(headerName, request.getHeader(headerName));
        }
    }

    private String readBody(HttpServletRequest request) {
        if (request.getMethod().equalsIgnoreCase("delete"))
            return "";
        try (BufferedReader reader = request.getReader()) {
            return reader.lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            LOGGER.error("Error while reading the request body", e);
            throw new RuntimeException(e);
        }
    }
}
