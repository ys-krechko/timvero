package org.timvero.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.timvero.controller.LoanScheduleRest;
import org.timvero.controller.LoanScheduleRestImpl;
import org.timvero.service.LoanCalculatorService;
import org.timvero.service.LoanCalculatorServiceImpl;

@Configuration
public class BeanConfig {

    @Bean
    public LoanCalculatorService loanCalculatorService() {
        return new LoanCalculatorServiceImpl();
    }

    @Bean
    public LoanScheduleRest loanScheduleRest(LoanCalculatorService calculator) {
        return new LoanScheduleRestImpl(calculator);
    }
}