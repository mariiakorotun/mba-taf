package com.mba.taf.tests.defects;

import com.mba.taf.business.api.AccountView;
import com.mba.taf.business.api.BankApiClient;
import com.mba.taf.business.api.OperationResult;
import com.mba.taf.business.fixtures.AccountFixture;
import com.mba.taf.data.factory.CredentialsFactory;
import com.mba.taf.tests.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Mini Banking App")
@Feature("Відомі дефекти безпеки")
public class KnownDefectsTest extends BaseTest {

    private AccountView adminAccount;

    @BeforeMethod(alwaysRun = true)
    public void prepareForeignAccount() {
        adminAccount = AccountFixture.createAccount(CredentialsFactory.admin(), "USD", new BigDecimal("100.00"));
    }

    @AfterMethod(alwaysRun = true)
    public void removeAccount() {
        AccountFixture.deleteQuietly(adminAccount != null ? adminAccount.id() : null);
        adminAccount = null;
    }

    @Test(groups = {"known-defect", "security", "api"},
            description = "DEF-01 IDOR: клієнт може зняти кошти з чужого рахунку")
    @Severity(SeverityLevel.BLOCKER)
    @Description("AccountController.withdraw/deposit не перевіряють, що рахунок належить поточному користувачу "
            + "(перевірка є лише в accountDetails). Очікувано: відмова, баланс чужого рахунку не змінюється.")
    public void userCannotWithdrawFromForeignAccount() {
        BankApiClient user = BankApiClient.loggedInAs(CredentialsFactory.user());
        OperationResult result = user.withdraw(adminAccount.id(), "100.00");

        BigDecimal adminBalance = BankApiClient.loggedInAs(CredentialsFactory.admin())
                .account(adminAccount.id()).orElseThrow().balance();
        assertThat(adminBalance)
                .as("[KNOWN-DEFECT] IDOR: баланс чужого рахунку змінився (відповідь: %s)", result)
                .isEqualByComparingTo("100.00");
    }

    @Test(groups = {"known-defect", "security", "api"},
            description = "DEF-02 @PreAuthorize не діє: клієнт може видалити будь-який рахунок")
    @Severity(SeverityLevel.BLOCKER)
    @Description("У SecurityConfig відсутня анотація @EnableMethodSecurity, тому @PreAuthorize(\"hasRole('ADMIN')\") "
            + "на /accounts/delete-direct ігнорується. Очікувано: 403 для ROLE_USER.")
    public void userCannotDeleteForeignAccount() {
        int status = BankApiClient.loggedInAs(CredentialsFactory.user()).deleteAccount(adminAccount.id());

        boolean stillExists = BankApiClient.loggedInAs(CredentialsFactory.admin()).account(adminAccount.id()).isPresent();
        assertThat(stillExists)
                .as("[KNOWN-DEFECT] Рахунок адміністратора видалено клієнтом (HTTP %d)", status)
                .isTrue();
    }

    @Test(groups = {"known-defect", "api"},
            description = "DEF-03 Сторінка деталей рахунку повертає 500")
    @Severity(SeverityLevel.NORMAL)
    @Description("AccountController.accountDetails повертає view «account-details», але шаблону "
            + "account-details.html немає в resources/templates.")
    public void accountDetailsPageIsAvailable() {
        int status = BankApiClient.loggedInAs(CredentialsFactory.admin())
                .get("/accounts/" + adminAccount.id()).statusCode();

        assertThat(status).as("[KNOWN-DEFECT] HTTP-статус /accounts/{id}").isEqualTo(200);
    }
}
