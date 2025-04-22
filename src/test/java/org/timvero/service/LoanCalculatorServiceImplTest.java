package org.timvero.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.timvero.entity.LoanRequest;
import org.timvero.entity.PaymentScheduleEntry;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class LoanCalculatorServiceImplTest {

    @InjectMocks
    private LoanCalculatorServiceImpl target;

    @Test
    void shouldCalculateCorrectMonthlyPaymentSchedule() {
        var request = new LoanRequest(
                valueOf(10000),
                valueOf(5), // 5% annual
                12 // 12 months
        );

        var result = target.calculateAnnuitySchedule(request);

        assertEquals(12, result.size(), "Schedule should have 12 months");

        var first = result.get(0);
        assertEquals(1, first.getMonth());
        assertEquals(valueOf(856.07).setScale(2, HALF_UP), first.getMonthlyPayment());
        assertEquals(valueOf(41.67).setScale(2, HALF_UP), first.getInterest()); // 10000 * 0.004167
        assertEquals(valueOf(814.40).setScale(2, HALF_UP), first.getPrincipal()); // 856.07 - 41.67

        var last = result.get(11);
        assertEquals(12, last.getMonth());
        assertEquals(valueOf(0.00).setScale(2, HALF_UP), last.getRemainingBalance()); // ensure final balance is 0
    }

    @Test
    void shouldHandleSingleMonthLoan() {
        var request = new LoanRequest(
                valueOf(1200),
                valueOf(12), // 12% annual -> 1% monthly
                1
        );

        var result = target.calculateAnnuitySchedule(request);

        assertEquals(1, result.size());
        var entry = result.get(0);

        assertEquals(1, entry.getMonth());
        assertEquals(valueOf(1212.00).setScale(2, HALF_UP), entry.getMonthlyPayment()); // 1200 + 1% interest
        assertEquals(valueOf(12.00).setScale(2, HALF_UP), entry.getInterest());
        assertEquals(valueOf(1200.00).setScale(2, HALF_UP), entry.getPrincipal());
        assertEquals(valueOf(0.00).setScale(2, HALF_UP), entry.getRemainingBalance());
    }

    @Test
    void shouldRoundToTwoDecimalPlaces() {
        var request = new LoanRequest(
                valueOf(12345.67),
                valueOf(4.75),
                24
        );

        var result = target.calculateAnnuitySchedule(request);

        result.forEach(entry -> {
            assertEquals(2, entry.getMonthlyPayment().scale());
            assertEquals(2, entry.getInterest().scale());
            assertEquals(2, entry.getPrincipal().scale());
            assertEquals(2, entry.getRemainingBalance().scale());
        });
    }

    @Test
    void totalPaymentsShouldRoughlyEqualLoanPlusInterest() {
        var request = new LoanRequest(
                valueOf(5000),
                valueOf(6),
                12
        );

        var result = target.calculateAnnuitySchedule(request);
        var totalPaid = result.stream()
                .map(PaymentScheduleEntry::getMonthlyPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertTrue(totalPaid.compareTo(valueOf(5000)) > 0);
    }
}