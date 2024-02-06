package br.com.sergioluigi.personalfinancialcontrol.application.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;

import java.util.UUID;

public interface FindCategoryById {
    CategoryModel execute(UUID id);
}
