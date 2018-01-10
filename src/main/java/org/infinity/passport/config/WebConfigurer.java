package org.infinity.passport.config;

import java.io.File;
import java.nio.file.Paths;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.infinity.passport.filter.CachingHttpHeadersFilter;
import org.infinity.passport.filter.RequestExecutionTimeFilter;
import org.infinity.passport.servlet.ResourceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.MetricsServlet;

/**
 * Web application configuration
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer, EmbeddedServletContainerCustomizer {

    private static final Logger   LOGGER = LoggerFactory.getLogger(WebConfigurer.class);

    @Autowired
    private Environment           env;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired(required = false)
    private MetricRegistry        metricRegistry;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (env.getActiveProfiles().length != 0) {
            LOGGER.info("Starting web application, using profiles: {}",
                    org.springframework.util.StringUtils.arrayToCommaDelimitedString(env.getActiveProfiles()));
        }
        EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD,
                DispatcherType.ASYNC);
        initMetrics(servletContext, disps);
        initResourceServlet(servletContext, disps);
        if (env.acceptsProfiles(ApplicationConstants.SPRING_PROFILE_PRODUCTION)) {
            initCachingHttpHeadersFilter(servletContext, disps);
        }
        LOGGER.info("Web application fully configured");
    }

    /**
     * Customize the Servlet engine: Mime types, the document root, the cache.
     */
    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        // IE issue
        mappings.add("html", MediaType.TEXT_HTML_VALUE + ";charset=utf-8");
        // CloudFoundry issue, see https://github.com/cloudfoundry/gorouter/issues/64
        mappings.add("json", MediaType.TEXT_HTML_VALUE + ";charset=utf-8");
        container.setMimeMappings(mappings);
        // When running in an IDE or with ./mvnw spring-boot:run, set location of the static web assets.
        setLocationForStaticAssets(container);
    }

    private void setLocationForStaticAssets(ConfigurableEmbeddedServletContainer container) {
        File root;
        String prefixPath = resolvePathPrefix();
        if (env.acceptsProfiles(ApplicationConstants.SPRING_PROFILE_PRODUCTION)) {
            root = new File(prefixPath + "target/www/");
        } else {
            root = new File(prefixPath + "src/main/webapp/");
        }
        if (root.exists() && root.isDirectory()) {
            container.setDocumentRoot(root);
        }
    }

    /**
     *  Resolve path prefix to static resources.
     */
    private String resolvePathPrefix() {
        String fullExecutablePath = this.getClass().getResource("").getPath();
        String rootPath = Paths.get(".").toUri().normalize().getPath();
        String extractedPath = fullExecutablePath.replace(rootPath, "");
        int extractionEndIndex = extractedPath.indexOf("target/");
        if (extractionEndIndex <= 0) {
            return "";
        }
        return extractedPath.substring(0, extractionEndIndex);
    }

    /**
     * Initializes the caching HTTP Headers Filter.
     */
    private void initCachingHttpHeadersFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        LOGGER.debug("Registering Caching HTTP Headers Filter");
        FilterRegistration.Dynamic cachingHttpHeadersFilter = servletContext.addFilter("cachingHttpHeadersFilter",
                new CachingHttpHeadersFilter(applicationProperties));

        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/content/*");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/app/*");
        cachingHttpHeadersFilter.setAsyncSupported(true);
        LOGGER.debug("Registered Caching HTTP Headers Filter");
    }

    /**
     * Initializes Metrics.
     */
    private void initMetrics(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        LOGGER.debug("Registering request execution time Filter");
        FilterRegistration.Dynamic requestExecutionTimeFilter = servletContext.addFilter("requestExecuteTimeFilter",
                new RequestExecutionTimeFilter());
        requestExecutionTimeFilter.addMappingForUrlPatterns(disps, true, "/api/*", "/open-api/*");
        requestExecutionTimeFilter.setAsyncSupported(true);
        LOGGER.debug("Registered request execution time Filter");

        LOGGER.debug("Initializing Metrics registries");
        servletContext.setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE, metricRegistry);
        servletContext.setAttribute(MetricsServlet.METRICS_REGISTRY, metricRegistry);
        LOGGER.debug("Initialized Metrics registries");

        LOGGER.debug("Registering Metrics Filter");
        FilterRegistration.Dynamic metricsFilter = servletContext.addFilter("webappMetricsFilter",
                new InstrumentedFilter());
        metricsFilter.addMappingForUrlPatterns(disps, true, "/*");
        metricsFilter.setAsyncSupported(true);
        LOGGER.debug("Registered Metrics Filter");

        LOGGER.debug("Registering Metrics Servlet");
        ServletRegistration.Dynamic metricsAdminServlet = servletContext.addServlet("metricsServlet",
                new MetricsServlet());
        metricsAdminServlet.addMapping("/management/app/metrics/*");
        metricsAdminServlet.setAsyncSupported(true);
        metricsAdminServlet.setLoadOnStartup(2);
        LOGGER.debug("Registered Metrics Servlet");
    }

    private void initResourceServlet(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        LOGGER.debug("Registering Resource Servlet");
        ServletRegistration.Dynamic metricsAdminServlet = servletContext.addServlet("resourceServlet",
                new ResourceServlet("developer", "developer"));
        metricsAdminServlet.addMapping("/developer/*");
        metricsAdminServlet.setAsyncSupported(true);
        metricsAdminServlet.setLoadOnStartup(3);
        LOGGER.debug("Registered Resource Servlet");
    }

    @Bean
    @ConditionalOnProperty(name = "application.cors.allowed-origins")
    public CorsFilter corsFilter() {
        LOGGER.debug("Registering CORS filter");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = applicationProperties.getCors();
        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/open-api/**", config);
        source.registerCorsConfiguration("/v2/api-docs", config);
        source.registerCorsConfiguration("/oauth/**", config);
        LOGGER.debug("Registered CORS filter");
        return new CorsFilter(source);
    }
}
