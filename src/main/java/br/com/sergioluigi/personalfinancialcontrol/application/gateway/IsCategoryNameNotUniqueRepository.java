package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

public interface IsCategoryNameNotUniqueRepository {
    Boolean check(String name);
}
