package br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.entity.CategoryEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.model.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository.FindCategoryByIdRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.repository.CategoryRepository;
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
