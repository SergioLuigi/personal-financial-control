package br.com.sergioluigi.personalfinancialcontrol.domain.category.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.model.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository.UpdateCategoryRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.service.IsCategoryNameUniqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCategory {

    private final FindCategoryById findCategoryById;

    private final UpdateCategoryRepository updateCategoryRepository;

    private final IsCategoryNameUniqueService isCategoryNameUniqueService;

    @Transactional
    public CategoryModel execute(UUID id, CategoryModel categoryModel) {
        var category = findCategoryById.execute(id);

        isCategoryNameUniqueService.check(category.getName(), categoryModel.getName());

        category.update(id, categoryModel);

        return updateCategoryRepository.execute(category);
    }
}
