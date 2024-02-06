package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;

public interface UpdateCategoryRepository {
    CategoryModel execute(CategoryModel categoryModel);
}
