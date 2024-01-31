package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository;

public interface IsCreditCardNameUniqueByUsernameRepository {
    Boolean check(String name, String username);
}
