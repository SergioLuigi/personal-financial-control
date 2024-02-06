package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;

import java.util.Optional;
import java.util.UUID;

public interface FindCategoryByIdRepository {
    Optional<CategoryModel> execute(UUID id);
}
