package com.lmm.tinkoff.task.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public TestCommons testCommons() {
        return new TestCommons();
    }
}
