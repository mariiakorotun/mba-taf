package com.mba.taf.data.provider;

import com.mba.taf.data.model.AmountCase;
import com.mba.taf.data.model.LoginCase;
import com.mba.taf.data.reader.JsonDataReader;
import org.testng.annotations.DataProvider;

public final class BankingDataProviders {

    public static final String LOGIN = "loginCases";
    public static final String DEPOSIT = "depositCases";
    public static final String WITHDRAW = "withdrawCases";

    private BankingDataProviders() {
    }

    @DataProvider(name = LOGIN)
    public static Object[][] loginCases() {
        return JsonDataReader.asDataProvider("testdata/login.json", LoginCase.class);
    }

    @DataProvider(name = DEPOSIT)
    public static Object[][] depositCases() {
        return JsonDataReader.asDataProvider("testdata/deposit.json", AmountCase.class);
    }

    @DataProvider(name = WITHDRAW)
    public static Object[][] withdrawCases() {
        return JsonDataReader.asDataProvider("testdata/withdraw.json", AmountCase.class);
    }
}
