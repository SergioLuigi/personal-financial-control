package br.com.sergioluigi.personalfinancialcontrol.usecase;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.IsCategoryNameUniqueRepository;
import br.com.sergioluigi.personalfinancialcontrol.application.usecase.IsCategoryNameUnique;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.CATEGORY_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class IsCategoryNameUniqueImpl implements IsCategoryNameUnique {

    private final IsCategoryNameUniqueRepository isCategoryNameUniqueRepository;

    @Override
    public void check(String name) {
        if (!isCategoryNameUniqueRepository.check(name)) {
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
