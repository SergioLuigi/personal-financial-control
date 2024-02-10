package br.com.sergioluigi.personalfinancialcontrol.application.usecase.category;

import br.com.sergioluigi.personalfinancialcontrol.usecase.category.CreateCategory;
import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.SaveCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCategoryImpl implements CreateCategory {

    private final SaveCategoryRepository categoryRepository;

    private final IsCategoryNameNotUniqueImpl isCategoryNameNotUniqueImpl;

    @Override
    @Transactional
    public CategoryModel execute(CategoryModel categoryModel) {

        isCategoryNameNotUniqueImpl.check(categoryModel.getName());

        return categoryRepository.execute(categoryModel);
    }

}
