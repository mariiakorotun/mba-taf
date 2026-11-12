package com.mba.taf.tests.api;

import com.mba.taf.business.api.BankApiClient;
import com.mba.taf.data.factory.CredentialsFactory;
import com.mba.taf.tests.base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Mini Banking App")
@Feature("Рольова модель доступу (RBAC)")
public class AccessControlApiTest extends BaseTest {

    @Test(groups = {"regression", "api", "security"},
            description = "TC-08 Клієнт (ROLE_USER) не має доступу до адмін-панелі")
    @Severity(SeverityLevel.CRITICAL)
    public void userCannotOpenAdminPanel() {
        Response response = BankApiClient.loggedInAs(CredentialsFactory.user()).get("/admin");

        assertThat(response.statusCode()).as("HTTP-статус /admin для ROLE_USER").isEqualTo(403);
    }

    @Test(groups = {"regression", "api", "security"},
            description = "TC-09 Адміністратор має доступ до адмін-панелі")
    @Severity(SeverityLevel.NORMAL)
    public void adminCanOpenAdminPanel() {
        Response response = BankApiClient.loggedInAs(CredentialsFactory.admin()).get("/admin");

        assertThat(response.statusCode()).as("HTTP-статус /admin для ROLE_ADMIN").isEqualTo(200);
        assertThat(response.asString()).contains("Керування доступом та рахунками");
    }

    @Test(groups = {"regression", "api", "security"},
            description = "TC-10 Невдалий вхід не створює автентифіковану сесію")
    @Severity(SeverityLevel.CRITICAL)
    public void failedLoginDoesNotAuthenticate() {
        BankApiClient client = BankApiClient.anonymous();
        boolean loggedIn = client.login(CredentialsFactory.unknownUser().username(), "Wr0ng#Pass");

        assertThat(loggedIn).as("Вхід неіснуючого користувача").isFalse();
        assertThat(client.get("/dashboard").statusCode()).as("Доступ до кабінету після невдалого входу").isEqualTo(302);
    }
}
