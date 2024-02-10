package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import java.util.UUID;

public interface IsAccountNameNotUniqueByUserIdRepository {
    Boolean execute(String name, UUID id);
}
