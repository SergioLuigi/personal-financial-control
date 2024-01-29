package br.com.sergioluigi.personalfinancialcontrol.domain.category.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository.DeleteCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCategory {

    private final FindCategoryById findCategoryById;

    private final DeleteCategoryRepository deleteCategoryRepository;

    @Transactional
    public void execute(UUID id) {
        deleteCategoryRepository.execute(findCategoryById.execute(id).getId());
    }
}
