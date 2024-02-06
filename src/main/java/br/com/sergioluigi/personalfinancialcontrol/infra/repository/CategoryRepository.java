package br.com.sergioluigi.personalfinancialcontrol.infra.repository;

import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    Boolean existsByName(String name);
}
