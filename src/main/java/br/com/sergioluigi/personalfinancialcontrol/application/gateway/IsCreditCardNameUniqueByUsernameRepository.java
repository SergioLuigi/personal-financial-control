package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

public interface IsCreditCardNameUniqueByUsernameRepository {
    Boolean check(String name, String username);
}
