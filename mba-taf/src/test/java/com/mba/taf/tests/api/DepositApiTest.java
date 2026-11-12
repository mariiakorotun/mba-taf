package com.mba.taf.tests.api;

import com.mba.taf.business.api.AccountView;
import com.mba.taf.business.api.BankApiClient;
import com.mba.taf.business.api.OperationResult;
import com.mba.taf.business.fixtures.AccountFixture;
import com.mba.taf.data.factory.CredentialsFactory;
import com.mba.taf.data.model.AmountCase;
import com.mba.taf.data.provider.BankingDataProviders;
import com.mba.taf.tests.base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;

@Epic("Mini Banking App")
@Feature("Поповнення рахунку")
public class DepositApiTest extends BaseTest {

    private BankApiClient client;
    private AccountView account;

    @BeforeMethod(alwaysRun = true)
    public void prepareAccount(Object[] testArgs) {
        AmountCase testCase = (AmountCase) testArgs[0];
        account = AccountFixture.createAccount(CredentialsFactory.user(), "UAH", testCase.initialBalanceValue());
        client = BankApiClient.loggedInAs(CredentialsFactory.user());
    }

    @AfterMethod(alwaysRun = true)
    public void removeAccount() {
        AccountFixture.deleteQuietly(account != null ? account.id() : null);
        account = null;
    }

    @Test(dataProvider = BankingDataProviders.DEPOSIT, dataProviderClass = BankingDataProviders.class,
            groups = {"regression", "api"},
            description = "TC-05 Поповнення рахунку: серверна валідація суми (Data-Driven: deposit.json)")
    @Severity(SeverityLevel.CRITICAL)
    public void deposit(AmountCase testCase) {
        OperationResult result = client.deposit(account.id(), testCase.amount());
        BigDecimal balanceAfter = client.account(account.id()).orElseThrow().balance();

        SoftAssertions soft = new SoftAssertions();
        switch (testCase.expected()) {
            case SUCCESS -> {
                soft.assertThat(result.httpStatus()).as("POST прийнято (PRG-редирект)").isEqualTo(302);
                soft.assertThat(result.successMessage()).as("Повідомлення про успіх").contains(testCase.expectedMessage());
                soft.assertThat(result.errorMessage()).as("Повідомлення про помилку").isEmpty();
            }
            case ERROR -> {
                soft.assertThat(result.httpStatus()).as("POST прийнято (PRG-редирект)").isEqualTo(302);
                soft.assertThat(result.errorMessage()).as("Повідомлення про помилку").contains(testCase.expectedMessage());
                soft.assertThat(result.successMessage()).as("Повідомлення про успіх").isEmpty();
            }
            case BAD_REQUEST -> soft.assertThat(result.httpStatus()).as("Некоректний запит відхилено").isEqualTo(400);
            default -> throw new IllegalArgumentException("Непідтримуваний результат для API: " + testCase.expected());
        }
        soft.assertThat(balanceAfter).as("Баланс після операції").isEqualByComparingTo(testCase.expectedBalanceValue());
        soft.assertAll();
    }
}
