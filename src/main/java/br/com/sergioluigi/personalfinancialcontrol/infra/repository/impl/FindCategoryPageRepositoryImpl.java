package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.infra.repository.CategoryRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.CategoryEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindCategoryPageRepository;
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
