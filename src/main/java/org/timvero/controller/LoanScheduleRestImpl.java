package org.timvero.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.timvero.entity.LoanRequest;
import org.timvero.entity.PaymentScheduleEntry;
import org.timvero.service.LoanCalculatorService;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class LoanScheduleRestImpl implements LoanScheduleRest {

    private final LoanCalculatorService calculator;

    @Override
    public ResponseEntity<List<PaymentScheduleEntry>> calculateSchedule(LoanRequest request) {
        log.info("Received request - {}.", request);
        var schedule = calculator.calculateAnnuitySchedule(request);
        log.info("Returning response.");

        return ResponseEntity.ok(schedule);
    }
}