package br.com.sergioluigi.personalfinancialcontrol.infra.repository;

import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID>,
        JpaSpecificationExecutor<AccountEntity> {

    Page<AccountEntity> findAllByUser_username(Pageable pageable, String username);

    boolean existsByNameAndUser_id(String name, UUID id);

    @Modifying
    @Query("update AccountEntity a set a.balance = :amount where a.id = :id")
    int updateBalanceById(@Param("id") UUID id, @Param("amount") Double amount);
}
