package br.com.sergioluigi.personalfinancialcontrol.domain.category.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.model.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository.SaveCategoryRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.service.IsCategoryNameUniqueService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCategory {

    private final SaveCategoryRepository categoryRepository;

    private final IsCategoryNameUniqueService isCategoryNameUniqueService;

    @Transactional
    public CategoryModel execute(CategoryModel categoryModel) {

        isCategoryNameUniqueService.check(categoryModel.getName());

        return categoryRepository.execute(categoryModel);
    }

}
