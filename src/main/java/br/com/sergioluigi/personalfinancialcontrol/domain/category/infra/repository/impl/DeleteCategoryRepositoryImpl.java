package br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository.DeleteCategoryRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeleteCategoryRepositoryImpl implements DeleteCategoryRepository {

    private final CategoryRepository categoryRepository;

    @Override
    public void execute(UUID id) {
        categoryRepository.deleteById(id);
    }
}
