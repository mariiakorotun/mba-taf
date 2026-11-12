package com.mba.taf.tests.gui;

import com.mba.taf.business.api.AccountView;
import com.mba.taf.business.fixtures.AccountFixture;
import com.mba.taf.business.pages.AccountsPage;
import com.mba.taf.business.pages.LoginPage;
import com.mba.taf.business.pages.TransactionsPage;
import com.mba.taf.business.pages.TransactionsPage.TransactionRow;
import com.mba.taf.data.factory.CredentialsFactory;
import com.mba.taf.data.model.Credentials;
import com.mba.taf.tests.base.BaseGuiTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.mba.taf.data.model.OperationType.DEPOSIT;
import static com.mba.taf.data.model.OperationType.WITHDRAW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Epic("Mini Banking App")
@Feature("Історія транзакцій")
public class TransactionHistoryGuiTest extends BaseGuiTest {

    private AccountView account;

    @BeforeMethod(alwaysRun = true)
    public void prepareAccount() {
        account = AccountFixture.createAccount(CredentialsFactory.user(), "UAH", BigDecimal.ZERO);
    }

    @AfterMethod(alwaysRun = true)
    public void removeAccount() {
        AccountFixture.deleteQuietly(account != null ? account.id() : null);
        account = null;
    }

    @Test(groups = {"regression", "gui", "e2e"},
            description = "TC-07 E2E: поповнення і зняття через UI відображаються в історії транзакцій")
    @Severity(SeverityLevel.CRITICAL)
    public void operationsAppearInHistory() {
        Credentials user = CredentialsFactory.user();
        AccountsPage accounts = new LoginPage(driver()).open()
                .loginAs(user.username(), user.password())
                .navBar().goToAccounts();

        accounts = accounts.deposit(account.id(), "250.00");
        assertThat(accounts.successMessage()).contains("Рахунок успішно поповнено!");

        accounts = accounts.withdraw(account.id(), "100.00");
        assertThat(accounts.successMessage()).contains("Кошти успішно знято!");
        assertThat(accounts.balanceOf(account.id())).as("Баланс після двох операцій")
                .isEqualByComparingTo("150.00");

        TransactionsPage history = accounts.navBar().goToTransactions();
        List<TransactionRow> rows = history.rowsForAccount(account.id());

        assertThat(rows).as("Транзакції тестового рахунку").hasSize(2);
        assertThat(rows)
                .map(TransactionRow::type, TransactionRow::signedAmount)
                .containsExactlyInAnyOrder(
                        tuple(DEPOSIT, new BigDecimal("250.00")),
                        tuple(WITHDRAW, new BigDecimal("-100.00")));
        assertThat(rows).map(TransactionRow::currency).containsOnly("UAH");
    }
}
