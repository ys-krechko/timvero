package org.timvero.service;

import org.springframework.stereotype.Service;
import org.timvero.entity.LoanRequest;
import org.timvero.entity.PaymentScheduleEntry;

import java.util.List;

@Service
public interface LoanCalculatorService {
    List<PaymentScheduleEntry> calculateAnnuitySchedule(LoanRequest request);
}