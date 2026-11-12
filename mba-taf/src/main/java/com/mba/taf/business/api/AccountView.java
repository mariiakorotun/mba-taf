package com.mba.taf.business.api;

import java.math.BigDecimal;

public record AccountView(long id, String type, BigDecimal balance, String currency) {
}
