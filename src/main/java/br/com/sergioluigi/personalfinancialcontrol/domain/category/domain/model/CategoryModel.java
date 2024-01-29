package br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel {

    private UUID id;

    private String name;

    private String description;

    private String color;

    private LocalDateTime createdOn;

    private LocalDateTime lastUpdateOn;

    public void update(UUID id, CategoryModel model) {
        this.id = id;
        this.name = model.getName();
        this.description = model.getDescription();
        this.color = model.getColor();
    }
}
