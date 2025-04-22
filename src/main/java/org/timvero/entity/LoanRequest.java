package org.timvero.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class LoanRequest {

    @NotNull(message = "Loan amount is required.")
    @DecimalMin(value = "0.01", message = "Loan amount must be greater than 0")
    private BigDecimal loanAmount;

    @NotNull(message = "Annual interest rate is required")
    @DecimalMin(value = "0.01", message = "Annual interest rate must be greater than 0")
    private BigDecimal annualInterestRate;

    @NotNull(message = "Term is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    private int termInMonth;
}