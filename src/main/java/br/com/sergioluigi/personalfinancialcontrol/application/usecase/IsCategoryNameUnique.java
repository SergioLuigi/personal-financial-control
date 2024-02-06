package br.com.sergioluigi.personalfinancialcontrol.application.usecase;

public interface IsCategoryNameUnique {
    void check(String name);

    void check(String currentName, String updatedName);
}
