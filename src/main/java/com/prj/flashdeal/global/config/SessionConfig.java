package com.prj.flashdeal.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Configuration
@EnableRedisIndexedHttpSession
public class SessionConfig {

    @Bean
    public ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean
    public SessionRegistry sessionRegistry(
        FindByIndexNameSessionRepository<? extends Session> sessionRepository
    ) {
        return new SpringSessionBackedSessionRegistry<>(sessionRepository);
    }
}
