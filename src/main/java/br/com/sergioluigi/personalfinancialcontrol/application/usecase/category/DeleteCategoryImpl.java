package br.com.sergioluigi.personalfinancialcontrol.application.usecase.category;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.DeleteCategoryRepository;
import br.com.sergioluigi.personalfinancialcontrol.usecase.category.DeleteCategory;
import br.com.sergioluigi.personalfinancialcontrol.usecase.category.FindCategoryById;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCategoryImpl implements DeleteCategory {

    private final FindCategoryById findCategoryById;

    private final DeleteCategoryRepository deleteCategoryRepository;

    @Override
    @Transactional
    public void execute(UUID id) {
        deleteCategoryRepository.execute(findCategoryById.execute(id).getId());
    }
}
