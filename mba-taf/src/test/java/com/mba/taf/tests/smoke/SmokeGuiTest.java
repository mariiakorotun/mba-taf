package com.mba.taf.tests.smoke;

import com.mba.taf.business.pages.HomePage;
import com.mba.taf.business.pages.LoginPage;
import com.mba.taf.tests.base.BaseGuiTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Mini Banking App")
@Feature("Smoke")
public class SmokeGuiTest extends BaseGuiTest {

    @Test(groups = {"smoke", "regression", "gui"},
            description = "TC-01 Публічна сторінка відкривається і веде на форму входу")
    @Severity(SeverityLevel.BLOCKER)
    public void publicPageLeadsToLogin() {
        HomePage home = new HomePage(driver()).open();

        assertThat(home.heroTitle()).as("Заголовок публічної сторінки").isEqualTo("Mini Banking App");
        assertThat(home.featureCount()).as("Кількість карток функціональних модулів").isEqualTo(3);

        LoginPage login = home.goToLogin();
        assertThat(login.isOpened()).as("Форма входу відображається").isTrue();
        assertThat(login.currentUrl()).endsWith("/login");
    }
}
