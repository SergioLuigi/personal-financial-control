package br.com.sergioluigi.personalfinancialcontrol.application.usecase.category;

import br.com.sergioluigi.personalfinancialcontrol.usecase.category.FindCategoryById;
import br.com.sergioluigi.personalfinancialcontrol.usecase.category.UpdateCategory;
import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.UpdateCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCategoryImpl implements UpdateCategory {

    private final FindCategoryById findCategoryById;

    private final UpdateCategoryRepository updateCategoryRepository;

    private final IsCategoryNameNotUniqueImpl isCategoryNameNotUniqueImpl;

    @Override
    @Transactional
    public CategoryModel execute(UUID id, CategoryModel categoryModel) {
        var category = findCategoryById.execute(id);

        isCategoryNameNotUniqueImpl.check(category.getName(), categoryModel.getName());

        category.update(id, categoryModel);

        return updateCategoryRepository.execute(category);
    }
}
