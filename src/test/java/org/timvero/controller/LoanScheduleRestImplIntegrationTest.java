package org.timvero.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.timvero.config.SecurityConfig;
import org.timvero.entity.LoanRequest;
import org.timvero.entity.PaymentScheduleEntry;
import org.timvero.service.LoanCalculatorService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoanScheduleRestImpl.class)
@AutoConfigureMockMvc()
@Import(SecurityConfig.class)
@ComponentScan("org.timvero")
class LoanScheduleRestImplIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanCalculatorService calculator;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 401 when user is not authenticated")
    void shouldReturn401IfNotUnauthorized() throws Exception {
        var request = new LoanRequest(new BigDecimal("1000"), new BigDecimal("5"), 12);

        mockMvc.perform(post("/api/loans/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 403 when user is authenticated without USER role")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturn403IfWrongRole() throws Exception {
        var request = new LoanRequest(new BigDecimal("1000"), new BigDecimal("5"), 12);

        mockMvc.perform(post("/api/loans/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 200 for authenticated user with USER role")
    @WithMockUser(username = "user", password = "123", roles = "USER")
    void shouldAllowAccessWithUserRole() throws Exception {
        var request = new LoanRequest(new BigDecimal("1000"), new BigDecimal("5"), 12);

        var schedule = List.of(
                new PaymentScheduleEntry(1, new BigDecimal("85.61"), new BigDecimal("81.94"), new BigDecimal("3.67"), new BigDecimal("918.06"))
        );

        when(calculator.calculateAnnuitySchedule(request)).thenReturn(schedule);

        mockMvc.perform(post("/api/loans/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andDo(print());
    }
}