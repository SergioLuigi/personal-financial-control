package br.com.sergioluigi.personalfinancialcontrol.usecase.category;

import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;

import java.util.UUID;

public interface UpdateCategory {
    CategoryModel execute(UUID id, CategoryModel categoryModel);
}
