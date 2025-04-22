package org.timvero.service;

import lombok.extern.slf4j.Slf4j;
import org.timvero.entity.LoanRequest;
import org.timvero.entity.PaymentScheduleEntry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;

@Slf4j
public class LoanCalculatorServiceImpl implements LoanCalculatorService {

    @Override
    public List<PaymentScheduleEntry> calculateAnnuitySchedule(LoanRequest request) {
        log.info("Starting calculation of schedule.");
        var monthlyRate = request.getAnnualInterestRate().divide(BigDecimal.valueOf(12 * 100), 10, HALF_UP);
        var monthlyPayment = calculateMonthlyPayment(request.getLoanAmount(), monthlyRate, request.getTermInMonth());
        var balance = request.getLoanAmount();
        List<PaymentScheduleEntry> schedule = new ArrayList<>();

        for (int month = 1; month <= request.getTermInMonth(); month++) {
            log.info("Processing {} payment month.", month);
            var interest = balance.multiply(monthlyRate).setScale(2, HALF_UP);
            var principle = monthlyPayment.subtract(interest).setScale(2, HALF_UP);

            balance = balance.subtract(principle).setScale(2, HALF_UP);

            if (month == request.getTermInMonth()) {
                log.info("Processing last payment month.");
                balance = ZERO;
            }

            schedule.add(new PaymentScheduleEntry(month,
                    monthlyPayment.setScale(2, HALF_UP),
                    principle.setScale(2, HALF_UP),
                    interest.setScale(2, HALF_UP),
                    balance.setScale(2, HALF_UP)));
        }
        log.info("Schedule calculated.");

        return schedule;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal loanAmount, BigDecimal rate, int term) {
        var onePlusRate = rate.add(ONE).pow(term);
        var denominator = onePlusRate.subtract(ONE);
        return loanAmount.multiply(rate).multiply(onePlusRate).divide(denominator, 2, HALF_UP);
    }
}
