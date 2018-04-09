package com.dbenjamin.keyprx.controller;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages = { "com.dbenjamin.keyprx.model" })
@EnableJpaRepositories(basePackages = { "com.dbenjamin.keyprx.repository" })
@EnableTransactionManagement
public class ApplicationConfiguration {


}
