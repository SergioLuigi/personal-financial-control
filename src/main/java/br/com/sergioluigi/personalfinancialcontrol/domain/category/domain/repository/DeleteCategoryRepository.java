package br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository;

import java.util.UUID;

public interface DeleteCategoryRepository {
    void execute(UUID id);
}
