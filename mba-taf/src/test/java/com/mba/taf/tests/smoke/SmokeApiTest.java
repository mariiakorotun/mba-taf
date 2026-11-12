package com.mba.taf.tests.smoke;

import com.mba.taf.business.api.BankApiClient;
import com.mba.taf.tests.base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Mini Banking App")
@Feature("Smoke")
public class SmokeApiTest extends BaseTest {

    @Test(groups = {"smoke", "regression", "api"},
            description = "TC-02 Сторінка входу доступна і захищена CSRF-токеном")
    @Severity(SeverityLevel.BLOCKER)
    public void loginPageIsAvailable() {
        Response response = BankApiClient.anonymous().get("/login");

        assertThat(response.statusCode()).as("HTTP-статус /login").isEqualTo(200);
        assertThat(response.asString())
                .as("Форма входу містить CSRF-токен")
                .contains("name=\"_csrf\"")
                .contains("name=\"username\"");
    }

    @Test(groups = {"smoke", "regression", "api", "security"},
            description = "TC-03 Анонімний користувач не має доступу до кабінету")
    @Severity(SeverityLevel.CRITICAL)
    public void anonymousIsRedirectedToLogin() {
        for (String protectedPath : new String[]{"/dashboard", "/accounts", "/transactions", "/admin"}) {
            Response response = BankApiClient.anonymous().get(protectedPath);
            assertThat(response.statusCode()).as("Статус для %s", protectedPath).isEqualTo(302);
            assertThat(response.getHeader("Location")).as("Редирект для %s", protectedPath).contains("/login");
        }
    }
}
