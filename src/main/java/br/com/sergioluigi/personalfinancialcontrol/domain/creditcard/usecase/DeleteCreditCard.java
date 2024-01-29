package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository.DeleteCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCreditCard {

    private final DeleteCategoryRepository deleteCategoryRepository;

    private final FindCreditCardById findCreditCardById;

    @Transactional
    public void execute(UUID id) {
        deleteCategoryRepository.execute(findCreditCardById.execute(id).getId());
    }
}
