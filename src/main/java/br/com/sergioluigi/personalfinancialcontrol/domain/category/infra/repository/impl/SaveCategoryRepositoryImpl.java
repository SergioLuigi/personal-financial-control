package br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.model.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository.SaveCategoryRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.entity.CategoryEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.repository.CategoryRepository;
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
