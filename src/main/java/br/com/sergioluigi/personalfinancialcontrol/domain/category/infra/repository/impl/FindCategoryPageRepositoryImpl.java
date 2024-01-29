package br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.entity.CategoryEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.model.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository.FindCategoryPageRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FindCategoryPageRepositoryImpl implements FindCategoryPageRepository {

    private final CategoryRepository categoryRepository;

    @Override
    public Page<CategoryModel> execute(PageRequest pageRequest) {
        return categoryRepository.findAll(pageRequest).map(CategoryEntity::toModel);
    }
}
