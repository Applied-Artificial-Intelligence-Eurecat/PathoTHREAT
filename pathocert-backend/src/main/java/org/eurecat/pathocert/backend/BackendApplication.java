package org.eurecat.pathocert.backend;

import org.eurecat.pathocert.backend.configuration.Configuration;
import org.eurecat.pathocert.backend.configuration.internal.exceptions.RequiredPropertyException;
import org.eurecat.pathocert.backend.configuration.internal.exceptions.UnparsableProperty;
import org.eurecat.pathocert.backend.users.model.Organization;
import org.eurecat.pathocert.backend.users.model.User;
import org.eurecat.pathocert.backend.users.model.UserRole;
import org.eurecat.pathocert.backend.users.repository.OrganizationRepository;
import org.eurecat.pathocert.backend.users.repository.UserRepository;
import org.eurecat.pathocert.backend.users.service.UserDetailsServiceImpl;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.concurrent.Executor;

import static org.eurecat.pathocert.backend.configuration.ConfigurationKt.parseProperties;

@SpringBootApplication
@EnableAsync
public class BackendApplication {

    //public static Configuration conf;

    public static void main(String[] args) throws RequiredPropertyException, UnparsableProperty {
        /*
        conf = parseProperties(args.length > 0 ? args[0] : "classpath://application.properties");
        System.out.println(conf.getSpringJpaShowSql());
         */
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserRepository userRepository, OrganizationRepository organizationRepository) {
        return args -> {
            organizationRepository.save(Organization.builder()
                    .name("PathoCERT")
                    .build());
            /*
            // Create corresponding user in DB
            User user = new User();
            user.setUsername("pathothreat_user_test");
            // Password is encoded
            user.setPassword("sfer3");
            user.setUserRole(UserRole.SUPER_ADMIN);

            System.out.println("USER CONFIG CORRECT");



            var org = organizationRepository.findAll().iterator();
            if (org.hasNext()){
                user.setOrganization(org.next());
            }
            System.out.println("ORG CONFIG CORRECT");
            userRepository.save(user);
             */
        };
    }

    @Bean
    public Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.initialize();
        return executor;
    }

    @Bean
    public StorageProvider storageProvider(JobMapper jobMapper) {
        InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();
        storageProvider.setJobMapper(jobMapper);
        return storageProvider;
    }
}
