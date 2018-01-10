//package org.infinity.passport.config;
//
//import org.infinity.passport.schedule.RemoveOldPersistentSessionJob;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//
//import com.dangdang.ddframe.job.api.JobConfiguration;
//import com.dangdang.ddframe.job.spring.schedule.SpringJobScheduler;
//import com.dangdang.ddframe.reg.zookeeper.ZookeeperConfiguration;
//import com.dangdang.ddframe.reg.zookeeper.ZookeeperRegistryCenter;
//
////@Configuration
//public class ScheduleConfiguration {
//
//    @Autowired
//    ApplicationProperties applicationProperties;
//    
//    @Value("${dubbo.registry.address}")
//    private String              zookeeperAddress;
//
//    @Bean(initMethod = "init")
//    public ZookeeperRegistryCenter getZookeeperRegistryCenter() {
//        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration();
//        zookeeperConfiguration.setServerLists(zookeeperAddress);
//        zookeeperConfiguration.setNamespace("elastic-job");
//        zookeeperConfiguration.setBaseSleepTimeMilliseconds(1000);
//        zookeeperConfiguration.setMaxSleepTimeMilliseconds(3000);
//        zookeeperConfiguration.setMaxRetries(3);
//        return new ZookeeperRegistryCenter(zookeeperConfiguration);
//    }
//
//    @Bean(initMethod = "init")
//    public SpringJobScheduler removeOldPersistentSessionJob() {
//        JobConfiguration jobConfiguration = new JobConfiguration(RemoveOldPersistentSessionJob.class.getName(),
//                RemoveOldPersistentSessionJob.class, 1, "0 0 0 * * ?");
//        jobConfiguration.setFailover(true);
//        //覆盖注册中心的数据
//        jobConfiguration.setOverwrite(true);
//        SpringJobScheduler jobScheduler = new SpringJobScheduler(getZookeeperRegistryCenter(), jobConfiguration);
//        return jobScheduler;
//    }
//}
