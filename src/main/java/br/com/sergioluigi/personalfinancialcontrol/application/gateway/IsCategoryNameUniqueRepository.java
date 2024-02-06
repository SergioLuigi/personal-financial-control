package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

public interface IsCategoryNameUniqueRepository {
    Boolean check(String name);
}
