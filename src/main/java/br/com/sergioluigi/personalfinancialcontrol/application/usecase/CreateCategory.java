package br.com.sergioluigi.personalfinancialcontrol.application.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;

public interface CreateCategory {
    CategoryModel execute(CategoryModel categoryModel);
}
