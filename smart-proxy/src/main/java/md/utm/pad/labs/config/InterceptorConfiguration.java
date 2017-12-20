package md.utm.pad.labs.config;

import md.utm.pad.labs.cache.CachingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by anrosca on Dec, 2017
 */
@Configuration
public class InterceptorConfiguration {

    @Autowired
    private CachingFilter cachingInterceptor;

    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(cachingInterceptor);
        registration.addUrlPatterns("/*");
        registration.setName("cachingInterceptor");
        registration.setOrder(1);
        return registration;
    }
}
