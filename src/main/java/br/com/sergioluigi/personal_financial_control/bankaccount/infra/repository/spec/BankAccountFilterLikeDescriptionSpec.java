package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec;

import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity.BankAccountJpaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public record BankAccountFilterLikeDescriptionSpec(
        @Nullable String description
) implements Specification<BankAccountJpaEntity> {

    @Override
    public @Nullable Predicate toPredicate(
            Root<BankAccountJpaEntity> root,
            CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {

        if (!StringUtils.hasText(description)) {
            return null;
        }

        return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("description")),
                LikePattern.containing(description),
                LikePattern.ESCAPE_CHARACTER
        );
    }
}
