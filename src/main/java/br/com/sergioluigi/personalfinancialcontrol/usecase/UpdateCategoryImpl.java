package br.com.sergioluigi.personalfinancialcontrol.usecase;

import br.com.sergioluigi.personalfinancialcontrol.application.usecase.FindCategoryById;
import br.com.sergioluigi.personalfinancialcontrol.application.usecase.UpdateCategory;
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

    private final IsCategoryNameUniqueImpl isCategoryNameUniqueImpl;

    @Override
    @Transactional
    public CategoryModel execute(UUID id, CategoryModel categoryModel) {
        var category = findCategoryById.execute(id);

        isCategoryNameUniqueImpl.check(category.getName(), categoryModel.getName());

        category.update(id, categoryModel);

        return updateCategoryRepository.execute(category);
    }
}
