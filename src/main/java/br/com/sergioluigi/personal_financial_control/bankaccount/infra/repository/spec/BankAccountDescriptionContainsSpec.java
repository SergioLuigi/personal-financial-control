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
 * Matches accounts whose description contains the text, ignoring case; no text matches every account.
 *
 * @param description the text to look for, or {@code null}
 */
public record BankAccountDescriptionContainsSpec(@Nullable String description) implements Specification<BankAccountJpaEntity> {

    /** Builds the predicate, or {@code null} when there is no text. */
    @Override
    public @Nullable Predicate toPredicate(Root<BankAccountJpaEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (description == null) {
            return null;
        }

        var escaped = description.toLowerCase(Locale.ROOT).replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");

        return cb.like(cb.lower(root.get("description")), "%" + escaped + "%", '\\');
    }
}
