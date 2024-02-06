package br.com.sergioluigi.personalfinancialcontrol.usecase;

import br.com.sergioluigi.personalfinancialcontrol.application.usecase.FindAccountPage;
import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindAccountPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindAccountPageImpl implements FindAccountPage {

    private final FindAccountPageRepository findAccountPageRepository;

    @Override
    public Page<AccountModel> execute(String username, Pageable pageable) {
        return findAccountPageRepository.execute(username, pageable);
    }
}
