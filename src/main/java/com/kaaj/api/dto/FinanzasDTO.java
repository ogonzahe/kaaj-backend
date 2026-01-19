package com.kaaj.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinanzasDTO {
    private String periodo;
    private LocalDate fecha;
    private BigDecimal ingresos;
    private BigDecimal egresos;
    private BigDecimal balance;

    public FinanzasDTO() {
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

    public BigDecimal getEgresos() {
        return egresos;
    }

    public void setEgresos(BigDecimal egresos) {
        this.egresos = egresos;
    }

    public BigDecimal getBalance() {
        if (ingresos != null && egresos != null) {
            return ingresos.subtract(egresos);
        }
        return BigDecimal.ZERO;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}