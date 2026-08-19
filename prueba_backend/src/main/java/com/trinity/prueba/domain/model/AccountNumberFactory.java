package com.trinity.prueba.domain.model;

import com.trinity.prueba.domain.model.enums.AccountType;
import java.util.Random;

/**
 * RN-P05 + RN-P06: Genera números de cuenta únicos de 10 dígitos.
 * - Cuentas de ahorro: prefijo "53"
 * - Cuentas corrientes: prefijo "33"
 */
public class AccountNumberFactory {

    private static final String SAVINGS_PREFIX = "53";
    private static final String CHECKING_PREFIX = "33";
    private static final int REMAINING_DIGITS = 8;
    private static final Random RANDOM = new Random();

    public static String generate(AccountType type) {
        if (type == null) {
            throw new IllegalArgumentException("El tipo de cuenta no puede ser nulo");
        }

        String prefix = switch (type) {
            case SAVINGS -> SAVINGS_PREFIX;
            case CHECKING -> CHECKING_PREFIX;
        };

        String randomDigits = String.format("%0" + REMAINING_DIGITS + "d",
                RANDOM.nextInt((int) Math.pow(10, REMAINING_DIGITS)));

        return prefix + randomDigits;
    }
}
