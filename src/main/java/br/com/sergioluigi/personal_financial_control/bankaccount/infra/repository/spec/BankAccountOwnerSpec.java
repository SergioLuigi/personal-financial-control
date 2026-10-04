package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec;

import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity.BankAccountJpaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 * Matches the accounts of one owner.
 *
 * @param owner the user who owns the accounts
 */
public record BankAccountOwnerSpec(String owner) implements Specification<BankAccountJpaEntity> {

    /** Builds the predicate on the {@code createdBy} audit column. */
    @Override
    public Predicate toPredicate(Root<BankAccountJpaEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get("audit").get("createdBy"), owner);
    }
}
