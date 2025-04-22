# Loan Schedule API – Documentation

## Overview
This API allows users to generate an annuity-based loan payment schedule based on user-provided inputs like loan amount, interest rate, and term in months.

The API is secured using **Basic Authentication**, and only users with the `USER` role can access it.

---

## Security

### Authentication Method
- **Type**: HTTP Basic Authentication
- **Scheme**: `basicAuth`
- **Role Required**: `USER`

> You can test the API via Swagger UI at `/swagger-ui.html` using:
>
> - **Username**: `user`  
> - **Password**: `123`

```yaml
Security:
  type: http
  scheme: basic
  roles:
    - USER
```

---

## Endpoint

### `POST /api/loans/schedule`

**Summary**: Generate a loan payment schedule.

**Security**: Requires Basic Auth (`ROLE_USER`)

**Request Body**:

```json
{
  "loanAmount": 10000.00,
  "annualInterestRate": 5.5,
  "termInMonth": 12
}
```

**Response**: `200 OK` – List of `PaymentScheduleEntry` objects.

**Sample Output**:

```json
[
  {
    "month": 1,
    "monthlyPayment": 856.07,
    "principal": 810.07,
    "interest": 46.00,
    "remainingBalance": 9189.93
  }
]
```

---

## Input Model – `LoanRequest`

| Field               | Type       | Validation                              | Description                      |
|--------------------|------------|------------------------------------------|----------------------------------|
| `loanAmount`       | `BigDecimal` | Required, > 0.01                          | Total loan amount to be borrowed |
| `annualInterestRate` | `BigDecimal` | Required, > 0.01                          | Annual interest rate in percent  |
| `termInMonth`      | `int`      | Required, ≥ 1                             | Term of the loan in months       |

---

## Output Model – `PaymentScheduleEntry`

| Field              | Type        | Description                                           |
|-------------------|-------------|-------------------------------------------------------|
| `month`           | `int`       | The payment number (starting from 1)                 |
| `monthlyPayment`  | `BigDecimal`| Fixed monthly annuity payment                        |
| `principal`       | `BigDecimal`| Portion of the payment applied to the principal      |
| `interest`        | `BigDecimal`| Portion of the payment applied to interest           |
| `remainingBalance`| `BigDecimal`| Remaining loan balance after the current payment     |

---

## Calculation Logic Summary

- **Monthly Interest Rate**: `annualInterestRate / 12 / 100`
- **Monthly Payment Formula**:

  monthlyPayment = P × r × (1 + r)^n / ((1 + r)^n - 1)

  Where:
  - `P` = loanAmount
  - `r` = monthlyRate
  - `n` = termInMonth

- Rounding: All monetary values are rounded to 2 decimal places using `RoundingMode.HALF_UP`.

---

## Security Configuration Summary

```java
// Basic Auth User
Username: user
Password: 123
Role: USER

// Security Rules
/api/loans/** → Requires ROLE_USER
/swagger-ui/**, /v3/api-docs/**, /actuator/** → Public
```

---

## Swagger Configuration

- **Path**: `/swagger-ui.html`
- **Authorization Button**: Available for Basic Auth
- **OpenAPI Security Requirement** is annotated via:

```java
@Operation(
    summary = "Generate loan payment schedule",
    security = @SecurityRequirement(name = "basicAuth")
)
```