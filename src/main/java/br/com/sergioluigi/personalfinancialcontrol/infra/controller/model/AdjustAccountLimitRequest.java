package br.com.sergioluigi.personalfinancialcontrol.infra.controller.model;

import jakarta.validation.constraints.NotNull;

public record AdjustAccountLimitRequest(@NotNull Double amount) {
}
