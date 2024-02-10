package br.com.sergioluigi.personalfinancialcontrol.usecase.category;

public interface IsCategoryNameNotUnique {
    void check(String name);

    void check(String currentName, String updatedName);
}
