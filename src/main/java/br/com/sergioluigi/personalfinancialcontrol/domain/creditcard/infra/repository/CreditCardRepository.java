package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.repository;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.entity.CreditCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreditCardRepository extends JpaRepository<CreditCardEntity, UUID> {
    Boolean existsByNameAndAccount_User_username(String name, String username);
}
