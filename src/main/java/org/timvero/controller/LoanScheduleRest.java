package org.timvero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.timvero.entity.LoanRequest;
import org.timvero.entity.PaymentScheduleEntry;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loan Schedule", description = "API for generating loan schedules")
public interface LoanScheduleRest {

    @Operation(summary = "Generate loan payment schedule", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200", description = "Successful calculation")
    @PostMapping("/schedule")
    ResponseEntity<List<PaymentScheduleEntry>> calculateSchedule(@Valid @RequestBody LoanRequest request);
}