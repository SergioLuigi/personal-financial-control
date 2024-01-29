package br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.repository;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID>,
        JpaSpecificationExecutor<AccountEntity> {

    Page<AccountEntity> findAllByUser_username(Pageable pageable, String username);

    boolean existsByNameAndUser_id(String name, UUID id);
}
