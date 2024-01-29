package br.com.sergioluigi.personalfinancialcontrol.domain.user.domain;

import java.util.Arrays;

public enum SubscriptionType {
    FREE,
    PREMIUM;

    public static String[] all() {
        return Arrays.stream(values()).map(Enum::name).toArray(String[]::new);
    }
}
