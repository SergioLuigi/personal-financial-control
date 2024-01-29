package br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.model.CategoryModel;

import java.util.Optional;
import java.util.UUID;

public interface FindCategoryByIdRepository {
    Optional<CategoryModel> execute(UUID id);
}
