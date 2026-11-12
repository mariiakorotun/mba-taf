package com.mba.taf.tests.gui;

import com.mba.taf.business.pages.DashboardPage;
import com.mba.taf.business.pages.LoginPage;
import com.mba.taf.core.config.ConfigManager;
import com.mba.taf.data.model.LoginCase;
import com.mba.taf.data.provider.BankingDataProviders;
import com.mba.taf.tests.base.BaseGuiTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Mini Banking App")
@Feature("Автентифікація")
public class LoginGuiTest extends BaseGuiTest {

    @Test(dataProvider = BankingDataProviders.LOGIN, dataProviderClass = BankingDataProviders.class,
            groups = {"regression", "gui"},
            description = "TC-04 Вхід у систему (Data-Driven: login.json)")
    @Severity(SeverityLevel.CRITICAL)
    public void login(LoginCase testCase) {
        LoginPage loginPage = new LoginPage(driver()).open();
        String username = testCase.resolvedUsername();
        String password = testCase.resolvedPassword();

        switch (testCase.expected()) {
            case SUCCESS -> {
                DashboardPage dashboard = loginPage.loginAs(username, password);
                boolean isAdmin = username.equals(ConfigManager.get("admin.username"));

                assertThat(dashboard.greetingUsername()).as("Ім'я у привітанні").isEqualTo(username);
                assertThat(dashboard.navBar().isAdminLinkVisible())
                        .as("Посилання «Адмін-панель» видно лише адміністратору").isEqualTo(isAdmin);
            }
            case ERROR -> {
                LoginPage afterError = loginPage.loginExpectingError(username, password);

                assertThat(afterError.isErrorShown()).as("Повідомлення про помилку відображено").isTrue();
                assertThat(afterError.errorMessage()).contains(testCase.expectedText());
            }
            case BLOCKED_BY_UI -> {
                assertThat(loginPage.isSubmitBlockedByBrowser(username, password))
                        .as("Браузер блокує відправлення форми з порожнім обов'язковим полем").isTrue();
                assertThat(loginPage.currentUrl()).as("Залишились на сторінці входу").endsWith("/login");
            }
            default -> throw new IllegalArgumentException("Непідтримуваний результат: " + testCase.expected());
        }
    }
}
