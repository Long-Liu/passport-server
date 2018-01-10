package org.infinity.passport.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SsoCorsFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SsoCorsFilter.class);

    public SsoCorsFilter() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        LOGGER.debug("Access URL: {}", request.getRequestURI());
        if (request.getRequestURI().endsWith("oauth/authorize") || request.getRequestURI().endsWith("login")
                || request.getRequestURI().endsWith("logout")) {
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers",
                    "Content-Type, X-XSRF-TOKEN, Cookie, Host, Origin, Referer, Accept, Upgrade-Insecure-Requests, "
                            + "X-Forwarded-For, X-Forwarded-Port, X-Forwarded-Proto, X-Requested-With");
        }

        // if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
        // response.setStatus(HttpServletResponse.SC_OK);
        // } else {
        chain.doFilter(req, res);
        // }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}