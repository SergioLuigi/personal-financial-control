package br.com.sergioluigi.personalfinancialcontrol.usecase.category;

import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;

public interface CreateCategory {
    CategoryModel execute(CategoryModel categoryModel);
}
