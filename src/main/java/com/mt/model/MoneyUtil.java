package com.mt.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MoneyUtil {

    private static Logger log = Logger.getLogger(MoneyUtil.class.getCanonicalName());

    //zero amount with scale 4 and financial rounding mode
    public static final BigDecimal zeroAmount = new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN);

    public static boolean validateCcyCode(String inputCcyCode) {
        try {
            Currency instance = Currency.getInstance(inputCcyCode);
            if (log.isLoggable(Level.FINEST)) {
                log.finest("Validate Currency Code: " + instance.getSymbol());
            }
            return instance.getCurrencyCode().equals(inputCcyCode);
        } catch (Exception e) {
            log.warning("Cannot parse the input Currency Code, Validation Failed: " + e.getLocalizedMessage());
        }
        return false;
    }

}
