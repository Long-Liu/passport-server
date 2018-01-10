//package org.infinity.passport.config;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.infinity.passport.setup.DatabaseInitialSetup;
//import org.infinity.passport.utils.JSR310DateConverters.DateToZonedDateTimeConverter;
//import org.infinity.passport.utils.JSR310DateConverters.ZonedDateTimeToDateConverter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.data.mongodb.config.EnableMongoAuditing;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
//import org.springframework.data.mongodb.core.convert.CustomConversions;
//import org.springframework.data.mongodb.core.convert.DbRefResolver;
//import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
//import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
//import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
//import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
//import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
//
//import com.github.mongobee.Mongobee;
//import com.mongodb.MongoClient;
//
//@Configuration
//@EnableMongoRepositories(ApplicationConstants.BASE_PACKAGE + ".repository")
//@EnableMongoAuditing(auditorAwareRef = "springSecurityAuditorAware")
//public class MongoConfiguration {
//
//    private final static Logger         LOGGER = LoggerFactory.getLogger(MongoConfiguration.class);
//
//    @Autowired
//    private MongoMappingContext  mongoMappingContext;
//
//    @Autowired
//    private SimpleMongoDbFactory mongoDbFactory;
//
//    @Bean
//    public LocalValidatorFactoryBean validator() {
//        return new LocalValidatorFactoryBean();
//    }
//
//    @Bean
//    public ValidatingMongoEventListener validatingMongoEventListener() {
//        return new ValidatingMongoEventListener(validator());
//    }
//
//    @Bean
//    public CustomConversions customConversions() {
//        List<Converter<?, ?>> converters = new ArrayList<>();
//        converters.add(DateToZonedDateTimeConverter.INSTANCE);
//        converters.add(ZonedDateTimeToDateConverter.INSTANCE);
//        return new CustomConversions(converters);
//    }
//
//    @Bean
//    public MappingMongoConverter mappingMongoConverter() throws Exception {
//        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
//        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
//        // remove _class field
//        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
//        converter.setCustomConversions(customConversions());
//        return converter;
//    }
//
//    @Bean
//    public MongoTemplate mongoTemplate() throws Exception {
//        return new MongoTemplate(mongoDbFactory, mappingMongoConverter());
//    }
//
////    @Bean
////    public Mongobee mongobee(MongoClient mongoClient, MongoTemplate mongoTemplate) {
////        LOGGER.debug("Configuring Mongobee");
////        Mongobee mongobee = new Mongobee(mongoClient);
////        // For embedded mongo
////        mongobee.setDbName(mongoClient.listDatabaseNames().first());
////        mongobee.setMongoTemplate(mongoTemplate);
////        mongobee.setChangeLogsScanPackage(DatabaseInitialSetup.class.getPackage().getName());
////        mongobee.setEnabled(true);
////        LOGGER.debug("Configured Mongobee");
////        return mongobee;
////    }
//}
