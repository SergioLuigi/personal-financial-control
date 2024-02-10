package br.com.sergioluigi.personalfinancialcontrol.application.usecase.category;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.IsCategoryNameNotUniqueRepository;
import br.com.sergioluigi.personalfinancialcontrol.usecase.category.IsCategoryNameNotUnique;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.CATEGORY_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class IsCategoryNameNotUniqueImpl implements IsCategoryNameNotUnique {

    private final IsCategoryNameNotUniqueRepository isCategoryNameNotUniqueRepository;

    @Override
    public void check(String name) {
        if (isCategoryNameNotUniqueRepository.check(name)) {
            throw new ApplicationException(CATEGORY_ALREADY_EXISTS)  ;
        }
    }

    @Override
    public void check(String currentName, String updatedName) {
        if (!currentName.equals(updatedName)) {
            check(updatedName);
        }
    }
}
