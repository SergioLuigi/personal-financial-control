package br.com.sergioluigi.personalfinancialcontrol.usecase;

import br.com.sergioluigi.personalfinancialcontrol.application.usecase.FindCategoryById;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindCategoryByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FindCategoryByIdImpl implements FindCategoryById {

    private final FindCategoryByIdRepository findCategoryByIdRepository;

    @Override
    public CategoryModel execute(UUID id) {
        return findCategoryByIdRepository.execute(id)
                .orElseThrow(() -> new ApplicationException(CATEGORY_NOT_FOUND));
    }
}
