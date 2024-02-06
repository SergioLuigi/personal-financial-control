package br.com.sergioluigi.personalfinancialcontrol.usecase;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.DeleteCategoryRepository;
import br.com.sergioluigi.personalfinancialcontrol.application.usecase.DeleteCreditCard;
import br.com.sergioluigi.personalfinancialcontrol.application.usecase.FindCreditCardById;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCreditCardImpl implements DeleteCreditCard {

    private final DeleteCategoryRepository deleteCategoryRepository;

    private final FindCreditCardById findCreditCardById;

    @Transactional
    public void execute(UUID id) {
        deleteCategoryRepository.execute(findCreditCardById.execute(id).getId());
    }
}
