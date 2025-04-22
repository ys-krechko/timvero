package org.timvero.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.timvero.entity.LoanRequest;
import org.timvero.entity.PaymentScheduleEntry;
import org.timvero.service.LoanCalculatorService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanScheduleRestImplTest {

    @InjectMocks
    private LoanScheduleRestImpl target;

    @Mock
    private LoanCalculatorService calculator;

    @Test
    void shouldReturnPaymentScheduleFromService() {
        var request = new LoanRequest(
                new BigDecimal("10000"),
                new BigDecimal("5"),
                12
        );

        var schedule = List.of(
                new PaymentScheduleEntry(1, new BigDecimal("856.07"), new BigDecimal("814.40"), new BigDecimal("41.67"), new BigDecimal("9185.60")),
                new PaymentScheduleEntry(2, new BigDecimal("856.07"), new BigDecimal("817.81"), new BigDecimal("38.26"), new BigDecimal("8367.79"))
        );

        when(calculator.calculateAnnuitySchedule(request)).thenReturn(schedule);

        var result = target.calculateSchedule(request);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(schedule, result.getBody());

        verify(calculator, times(1)).calculateAnnuitySchedule(request);
    }

    @Test
    void shouldReturnEmptyListIfServiceReturnsEmptySchedule() {
        var request = new LoanRequest(new BigDecimal("5000"), new BigDecimal("3"), 6);

        when(calculator.calculateAnnuitySchedule(request)).thenReturn(List.of());

        var result = target.calculateSchedule(request);

        assertNotNull(result);
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());
        verify(calculator).calculateAnnuitySchedule(request);
    }
}