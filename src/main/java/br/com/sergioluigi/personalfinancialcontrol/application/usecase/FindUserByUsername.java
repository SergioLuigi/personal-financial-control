package br.com.sergioluigi.personalfinancialcontrol.application.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;

public interface FindUserByUsername {
    public UserModel execute(String username);
}
