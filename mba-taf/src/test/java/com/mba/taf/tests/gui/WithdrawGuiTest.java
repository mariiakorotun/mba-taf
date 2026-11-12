package com.mba.taf.tests.gui;

import com.mba.taf.business.api.AccountView;
import com.mba.taf.business.fixtures.AccountFixture;
import com.mba.taf.business.pages.AccountsPage;
import com.mba.taf.business.pages.LoginPage;
import com.mba.taf.data.factory.CredentialsFactory;
import com.mba.taf.data.model.AmountCase;
import com.mba.taf.data.model.Credentials;
import com.mba.taf.data.model.OperationType;
import com.mba.taf.data.provider.BankingDataProviders;
import com.mba.taf.tests.base.BaseGuiTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Mini Banking App")
@Feature("Зняття коштів")
public class WithdrawGuiTest extends BaseGuiTest {

    private AccountView account;

    @BeforeMethod(alwaysRun = true)
    public void prepareAccount(Object[] testArgs) {
        AmountCase testCase = (AmountCase) testArgs[0];
        account = AccountFixture.createAccount(CredentialsFactory.user(), "UAH", testCase.initialBalanceValue());
    }

    @AfterMethod(alwaysRun = true)
    public void removeAccount() {
        AccountFixture.deleteQuietly(account != null ? account.id() : null);
        account = null;
    }

    @Test(dataProvider = BankingDataProviders.WITHDRAW, dataProviderClass = BankingDataProviders.class,
            groups = {"regression", "gui"},
            description = "TC-06 Зняття коштів через UI (Data-Driven: withdraw.json)")
    @Severity(SeverityLevel.CRITICAL)
    public void withdraw(AmountCase testCase) {
        Credentials user = CredentialsFactory.user();
        AccountsPage accounts = new LoginPage(driver()).open()
                .loginAs(user.username(), user.password())
                .navBar().goToAccounts();
        assertThat(accounts.balanceOf(account.id()))
                .as("Pre-condition: початковий баланс").isEqualByComparingTo(testCase.initialBalanceValue());

        SoftAssertions soft = new SoftAssertions();
        switch (testCase.expected()) {
            case SUCCESS -> {
                accounts = accounts.withdraw(account.id(), testCase.amount());
                soft.assertThat(accounts.successMessage()).contains(testCase.expectedMessage());
                soft.assertThat(accounts.errorMessage()).isEmpty();
            }
            case ERROR -> {
                accounts = accounts.withdraw(account.id(), testCase.amount());
                soft.assertThat(accounts.errorMessage()).contains(testCase.expectedMessage());
                soft.assertThat(accounts.successMessage()).isEmpty();
            }
            case BLOCKED_BY_UI -> {
                soft.assertThat(accounts.isAmountAcceptedByBrowser(account.id(), OperationType.WITHDRAW, testCase.amount()))
                        .as("Браузер блокує відправлення некоректної суми").isFalse();
                accounts = accounts.open();   // перечитати баланс із сервера
            }
            default -> throw new IllegalArgumentException("Непідтримуваний результат для UI: " + testCase.expected());
        }
        soft.assertThat(accounts.balanceOf(account.id()))
                .as("Баланс після операції").isEqualByComparingTo(testCase.expectedBalanceValue());
        soft.assertAll();
    }
}
