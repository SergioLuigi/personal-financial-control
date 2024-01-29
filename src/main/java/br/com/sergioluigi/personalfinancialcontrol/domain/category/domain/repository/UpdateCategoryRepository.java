package br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.model.CategoryModel;

public interface UpdateCategoryRepository {
    CategoryModel execute(CategoryModel categoryModel);
}
