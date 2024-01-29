package br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.service;

import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository.IsCategoryNameUniqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.CATEGORY_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class IsCategoryNameUniqueService {

    private final IsCategoryNameUniqueRepository isCategoryNameUniqueRepository;

    public void check(String name) {
        if (!isCategoryNameUniqueRepository.check(name)) {
            throw new ApplicationException(CATEGORY_ALREADY_EXISTS)  ;
        }
    }

    public void check(String currentName, String updatedName) {
        if (!currentName.equals(updatedName)) {
            check(updatedName);
        }
    }
}
