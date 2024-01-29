package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository;

public interface IsCreditCardUniqueByNameRepository {
    Boolean check(String name);
}
