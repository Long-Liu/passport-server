package org.infinity.passport.config;

import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Springfox Swagger configuration.
 *
 * Warning! When having a lot of REST endpoints, Springfox can become a
 * performance issue. In that case, you can use a specific Spring profile for
 * this class, so that only front-end developers have access to the Swagger
 * view.
 */
@Configuration
@EnableSwagger2
@Profile("!" + ApplicationConstants.SPRING_PROFILE_NO_SWAGGER)
public class SwaggerConfiguration {

    private static final Logger   LOGGER                           = LoggerFactory
            .getLogger(SwaggerConfiguration.class);

    @Autowired
    private ApplicationProperties applicationProperties;

    public static final String    DEFAULT_API_INCLUDE_PATTERN      = "/api/.*";

    public static final String    DEFAULT_OPEN_API_INCLUDE_PATTERN = "/open-api/.*";

    @Bean
    public Docket apiDocket() {
        LOGGER.debug("Starting Swagger API docket");
        StopWatch watch = new StopWatch();
        watch.start();

        Docket docket = new Docket(DocumentationType.SWAGGER_2).groupName("api-group").apiInfo(apiInfo())
                .forCodeGeneration(true);
        if (System.getProperty("specified.uri.scheme.host") != null
                && "true".equals(System.getProperty("specified.uri.scheme.host"))) {
            docket.host(applicationProperties.getSwagger().getHost());
        }
        docket.genericModelSubstitutes(ResponseEntity.class).ignoredParameterTypes(java.sql.Date.class)
                .directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
                .directModelSubstitute(java.time.LocalDateTime.class, Date.class).select()
                .paths(regex(DEFAULT_API_INCLUDE_PATTERN)).build();
        watch.stop();
        LOGGER.debug("Started Swagger API docket in {} ms", watch.getTotalTimeMillis());
        return docket;
    }

    @Bean
    public Docket openApiDocket() {
        LOGGER.debug("Starting Swagger Open API docket");
        StopWatch watch = new StopWatch();
        watch.start();

        Docket docket = new Docket(DocumentationType.SWAGGER_2).groupName("open-api-group").apiInfo(openApiInfo())
                .forCodeGeneration(true);
        if (System.getProperty("specified.uri.scheme.host") != null
                && "true".equals(System.getProperty("specified.uri.scheme.host"))) {
            docket.host(applicationProperties.getSwagger().getHost());
        }
        docket.genericModelSubstitutes(ResponseEntity.class).ignoredParameterTypes(Pageable.class)
                .ignoredParameterTypes(java.sql.Date.class)
                .directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
                .directModelSubstitute(java.time.LocalDateTime.class, Date.class).select()
                .paths(regex(DEFAULT_OPEN_API_INCLUDE_PATTERN)).build();
        watch.stop();
        LOGGER.debug("Started Swagger Open API docket in {} ms", watch.getTotalTimeMillis());
        return docket;
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact(applicationProperties.getSwagger().getContactName(),
                applicationProperties.getSwagger().getContactUrl(),
                applicationProperties.getSwagger().getContactEmail());

        ApiInfo apiInfo = new ApiInfo(applicationProperties.getSwagger().getApi().getTitle(),
                applicationProperties.getSwagger().getApi().getDescription(),
                applicationProperties.getSwagger().getVersion(),
                applicationProperties.getSwagger().getTermsOfServiceUrl(), contact,
                applicationProperties.getSwagger().getLicense(), applicationProperties.getSwagger().getLicenseUrl());
        return apiInfo;
    }

    private ApiInfo openApiInfo() {
        Contact contact = new Contact(applicationProperties.getSwagger().getContactName(),
                applicationProperties.getSwagger().getContactUrl(),
                applicationProperties.getSwagger().getContactEmail());

        ApiInfo apiInfo = new ApiInfo(applicationProperties.getSwagger().getOpenApi().getTitle(),
                applicationProperties.getSwagger().getOpenApi().getDescription(),
                applicationProperties.getSwagger().getVersion(),
                applicationProperties.getSwagger().getTermsOfServiceUrl(), contact,
                applicationProperties.getSwagger().getLicense(), applicationProperties.getSwagger().getLicenseUrl());
        return apiInfo;
    }
}
