package br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository;

public interface IsCategoryNameUniqueRepository {
    Boolean check(String name);
}
