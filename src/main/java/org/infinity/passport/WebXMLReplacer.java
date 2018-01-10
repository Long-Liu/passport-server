package org.infinity.passport;

import org.infinity.passport.utils.ApplicationPropertiesUtils;
import org.infinity.passport.utils.LogUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;

/**
 * A replacement of web.xml
 *
 */
public class WebXMLReplacer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PassportLauncher.class);
    }

    @Override
    protected WebApplicationContext run(SpringApplication application) {
        WebApplicationContext webApplicationContext = null;
        try {
            ApplicationPropertiesUtils.addDefaultProfile(application);
            ApplicationPropertiesUtils.addEnvVariables();
            webApplicationContext = (WebApplicationContext) application.run();
            Environment env = webApplicationContext.getEnvironment();
            PassportLauncher.checkProfiles(env);
            PassportLauncher.printServerInfo(env);
            PassportLauncher.addShutdownHook();
        } catch (Exception e) {
            LogUtils.error(e, "Application start failed");
        }
        return webApplicationContext;
    }
}
