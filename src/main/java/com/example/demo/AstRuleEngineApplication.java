package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AstRuleEngineApplication {

	
	@Autowired
    private RuleService ruleService;
	
	public static void main(String[] args) {
		SpringApplication.run(AstRuleEngineApplication.class, args);
	}
	
	@Bean
    public CommandLineRunner init() {
        return args -> {
            ruleService.insertInitialRules();
        };
    }

}
