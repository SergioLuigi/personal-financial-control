package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

public interface GetAddedAccountBalancesRepository {
    Double execute(String username);
}
