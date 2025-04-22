package org.timvero.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentScheduleEntry {
    private int month;
    private BigDecimal monthlyPayment;
    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal remainingBalance;
}