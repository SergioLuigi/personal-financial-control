package br.com.sergioluigi.personalfinancialcontrol.domain.account.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository.FindAccountPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindAccountPage {

    private final FindAccountPageRepository findAccountPageRepository;

    public Page<AccountModel> execute(String username, Pageable pageable) {
        return findAccountPageRepository.execute(username, pageable);
    }
}
