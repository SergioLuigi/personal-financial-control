package br.com.sergioluigi.personalfinancialcontrol.usecase.user;

import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;

public interface FindUserByUsername {
    public UserModel execute(String username);
}
