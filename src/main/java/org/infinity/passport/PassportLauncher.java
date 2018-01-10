package org.infinity.passport;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.config.ApplicationConstants;
import org.infinity.passport.config.ApplicationProperties;
import org.infinity.passport.utils.ApplicationPropertiesUtils;
import org.infinity.passport.utils.CleanWorkThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.util.Assert;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class })
public class PassportLauncher extends WebMvcConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PassportLauncher.class);

    /**
     * Main method, used to run the application. Spring profiles can be
     * configured with a program arguments
     * --spring.profiles.active=your-active-profile
     * 
     * @param args
     * @throws UnknownHostException 
     */
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(PassportLauncher.class);
        convertToSystemProperties(args);
        ApplicationPropertiesUtils.addDefaultProfile(app);
        ApplicationPropertiesUtils.addEnvVariables();
        Environment env = app.run(args).getEnvironment();
        checkProfiles(env);
        printServerInfo(env);
        addShutdownHook();
    }

    private static void convertToSystemProperties(String[] args) {
        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
        if (source.containsProperty("logging.path")) {
            System.setProperty("logging.path", source.getProperty("logging.path"));
        }
        if (source.containsProperty("logback.loglevel")) {
            System.setProperty("logback.loglevel", source.getProperty("logback.loglevel"));
        }
    }

    public static void printServerInfo(Environment env) throws UnknownHostException {
        // @formatter:off
        String serverInfo = MessageFormat.format(
                "Application is running:\n"
                + "------------------------------------------------------------------------\n\t"
                + "Application:     {0}\n\t"
                + "Local IP:        {1}://127.0.0.1:{2}{3}\n\t"
                + "Public IP:       {4}://{5}:{6}{7}\n\t"
                + "Profiles:        {8}\n\t"
                + "PID:             {9}\n\t"
                + "Log:             {10}\n"
                + "------------------------------------------------------------------------",
                env.getProperty("spring.application.name"),
                StringUtils.isEmpty(env.getProperty("server.ssl.key-store")) ? "http" : "https",
                env.getProperty("server.port"),
                StringUtils.isEmpty(env.getProperty("server.context-path")) ? "" : env.getProperty("server.context-path"),
                StringUtils.isEmpty(env.getProperty("server.ssl.key-store")) ? "http" : "https",
                        InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                StringUtils.isEmpty(env.getProperty("server.context-path")) ? "" : env.getProperty("server.context-path"),
                org.springframework.util.StringUtils.arrayToCommaDelimitedString(env.getActiveProfiles()),
                System.getProperty("PID"),
                System.getProperty("logging.path") + IOUtils.DIR_SEPARATOR + env.getProperty("info.artifact.id") + ".log");
        
        // @formatter:on
        LOGGER.info(serverInfo);
    }

    public static void checkProfiles(Environment env) {
        Assert.notEmpty(env.getActiveProfiles(), "No Spring profile configured.");
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        activeProfiles.forEach(activeProfile -> {
            if (!ArrayUtils.contains(ApplicationConstants.AVAILABLE_PROFILES, activeProfile)) {
                LOGGER.error("You have misconfigured your application with an illegal profile '{}'!", activeProfile);
                System.exit(0);
            }
        });
        if (activeProfiles.contains(ApplicationConstants.SPRING_PROFILE_DEVELOPMENT)
                && activeProfiles.contains(ApplicationConstants.SPRING_PROFILE_PRODUCTION)) {
            LOGGER.error("You have misconfigured your application! "
                    + "It should not run with both the 'dev' and 'prod' profiles at the same time.");
            System.exit(0);
        }
    }

    public static void addShutdownHook() {
        // Add hook thread to runtime
        Runtime.getRuntime().addShutdownHook(new CleanWorkThread());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/oauth/confirm_access").setViewName("authorize");
    }
}
