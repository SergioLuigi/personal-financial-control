package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.SaveCategoryRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.CategoryRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.CategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SaveCategoryRepositoryImpl implements SaveCategoryRepository {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryModel execute(CategoryModel categoryModel) {
        return categoryRepository.save(CategoryEntity.createNewEntityOf(categoryModel)).toModel();
    }
}
