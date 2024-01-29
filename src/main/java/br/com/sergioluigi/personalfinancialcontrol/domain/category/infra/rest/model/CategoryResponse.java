package br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.rest.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.model.CategoryModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private UUID id;

    private String name;

    private String description;

    private String color;

    public CategoryResponse(CategoryModel categoryModel) {
        this.id = categoryModel.getId();
        this.name = categoryModel.getName();
        this.description = categoryModel.getDescription();
        this.color = categoryModel.getColor();
    }
}
