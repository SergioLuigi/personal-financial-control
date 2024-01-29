package br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository.IsCategoryNameUniqueRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IsCategoryNameUniqueRepositoryImpl implements IsCategoryNameUniqueRepository {

    private final CategoryRepository categoryRepository;

    @Override
    public Boolean check(String name) {
        return !categoryRepository.existsByName(name);
    }
}
