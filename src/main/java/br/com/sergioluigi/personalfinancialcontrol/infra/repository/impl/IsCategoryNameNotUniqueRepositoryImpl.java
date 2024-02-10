package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.IsCategoryNameNotUniqueRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IsCategoryNameNotUniqueRepositoryImpl implements IsCategoryNameNotUniqueRepository {

    private final CategoryRepository categoryRepository;

    @Override
    public Boolean check(String name) {
        return categoryRepository.existsByName(name);
    }
}
