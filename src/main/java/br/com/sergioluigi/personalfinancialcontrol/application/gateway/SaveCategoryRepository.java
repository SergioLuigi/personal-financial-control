package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;

public interface SaveCategoryRepository {
    CategoryModel execute(CategoryModel categoryModel);
}
