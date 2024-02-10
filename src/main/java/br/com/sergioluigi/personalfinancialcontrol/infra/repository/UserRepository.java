package br.com.sergioluigi.personalfinancialcontrol.infra.repository;

import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
}
