package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec;

import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity.BankAccountJpaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

/**
 * Matches accounts whose name contains the text, ignoring case; no text matches every account.
 *
 * @param name the text to look for, or {@code null}
 */
public record BankAccountNameContainsSpec(@Nullable String name) implements Specification<BankAccountJpaEntity> {

    /** Builds the predicate, or {@code null} when there is no text. */
    @Override
    public @Nullable Predicate toPredicate(Root<BankAccountJpaEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (name == null) {
            return null;
        }

        var escaped = name.toLowerCase(Locale.ROOT).replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");

        return cb.like(cb.lower(root.get("name")), "%" + escaped + "%", '\\');
    }
}
