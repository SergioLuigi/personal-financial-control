package br.com.sergioluigi.personalfinancialcontrol.domain.category.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.model.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository.FindCategoryByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FindCategoryById {

    private final FindCategoryByIdRepository findCategoryByIdRepository;

    public CategoryModel execute(UUID id) {
        return findCategoryByIdRepository.execute(id)
                .orElseThrow(() -> new ApplicationException(CATEGORY_NOT_FOUND));
    }
}
