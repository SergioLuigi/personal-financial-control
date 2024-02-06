package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.infra.repository.CategoryRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.CategoryEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindCategoryByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FindCategoryByIdRepositoryImpl implements FindCategoryByIdRepository {

    private final CategoryRepository categoryRepository;

    @Override
    public Optional<CategoryModel> execute(UUID id) {
        return categoryRepository.findById(id).map(CategoryEntity::toModel);
    }
}
