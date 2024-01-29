package br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository;

import java.util.UUID;

public interface IsAccountNameUniqueByUserIdRepository {
    Boolean execute(String name, UUID id);
}
