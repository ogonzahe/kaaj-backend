package com.kaaj.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FinanzasDTO {
    private String periodo;
    private LocalDate fecha;
    private BigDecimal ingresos;
    private BigDecimal egresos;
    private BigDecimal balance;

    // Custom getter with business logic - overrides Lombok's generated getter
    public BigDecimal getBalance() {
        if (ingresos != null && egresos != null) {
            return ingresos.subtract(egresos);
        }
        return BigDecimal.ZERO;
    }
}
