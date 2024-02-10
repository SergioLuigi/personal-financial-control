package br.com.sergioluigi.personalfinancialcontrol.usecase.category;

import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;

import java.util.UUID;

public interface FindCategoryById {
    CategoryModel execute(UUID id);
}
