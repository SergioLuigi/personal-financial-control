package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import java.util.UUID;

public interface DeleteCategoryRepository {
    void execute(UUID id);
}
