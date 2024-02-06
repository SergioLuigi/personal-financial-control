package br.com.sergioluigi.personalfinancialcontrol.infra.controller.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CategoryRequest(
        UUID id,
        @NotNull
        String name,
        @NotNull
        String description,
        @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")
        String color
) {
    public CategoryModel toModel() {
        return CategoryModel.builder()
                        .name(name)
                        .description(description)
                        .color(color)
                        .build();
    }
}
