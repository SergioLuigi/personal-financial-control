package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec;

import java.util.Locale;

final class LikePattern {

    static final char ESCAPE_CHARACTER = '\\';

    private LikePattern() {
    }

    static String containing(String term) {
        return "%" + escape(term.toLowerCase(Locale.ROOT)) + "%";
    }

    private static String escape(String term) {
        return term.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
