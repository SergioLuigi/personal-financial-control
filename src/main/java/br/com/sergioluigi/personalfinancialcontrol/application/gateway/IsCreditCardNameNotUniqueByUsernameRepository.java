package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

public interface IsCreditCardNameNotUniqueByUsernameRepository {
    Boolean check(String name, String username);
}
