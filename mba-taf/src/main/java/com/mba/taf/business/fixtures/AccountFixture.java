package com.mba.taf.business.fixtures;

import com.mba.taf.business.api.AccountView;
import com.mba.taf.business.api.BankApiClient;
import com.mba.taf.business.api.OperationResult;
import com.mba.taf.core.config.ConfigManager;
import com.mba.taf.data.factory.CredentialsFactory;
import com.mba.taf.data.model.Credentials;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public final class AccountFixture {

    private static final Logger LOG = LoggerFactory.getLogger(AccountFixture.class);
    private static final Object CREATE_LOCK = new Object();

    private AccountFixture() {
    }

    @Step("Pre-condition: створити новий рахунок {currency} для {owner} з балансом {initialBalance}")
    public static AccountView createAccount(Credentials owner, String currency, BigDecimal initialBalance) {
        guardAgainstReadOnlyEnvironment();
        BankApiClient admin = BankApiClient.loggedInAs(CredentialsFactory.admin());
        BankApiClient ownerClient = BankApiClient.loggedInAs(owner);

        AccountView created;
        synchronized (CREATE_LOCK) {
            Set<Long> before = ownerClient.accounts().stream().map(AccountView::id).collect(Collectors.toSet());
            OperationResult result = admin.createAccount(admin.findUserId(owner.username()), currency);
            if (!result.isSuccess()) {
                throw new IllegalStateException("Не вдалося створити рахунок: " + result);
            }
            created = ownerClient.accounts().stream()
                    .filter(a -> !before.contains(a.id()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Новий рахунок не з'явився у списку користувача"));
        }

        if (initialBalance.signum() > 0) {
            OperationResult deposit = ownerClient.deposit(created.id(), initialBalance.toPlainString());
            if (!deposit.isSuccess()) {
                throw new IllegalStateException("Не вдалося встановити початковий баланс: " + deposit);
            }
        }
        AccountView ready = ownerClient.account(created.id()).orElseThrow();
        LOG.info("Підготовлено рахунок #{} ({}) для '{}' з балансом {}",
                ready.id(), ready.currency(), owner.username(), ready.balance());
        return ready;
    }

    @Step("Post-condition: видалити тестовий рахунок #{accountId}")
    public static void deleteQuietly(Long accountId) {
        if (accountId == null) {
            return;
        }
        try {
            int status = BankApiClient.loggedInAs(CredentialsFactory.admin()).deleteAccount(accountId);
            LOG.info("Рахунок #{} видалено (HTTP {})", accountId, status);
        } catch (RuntimeException e) {
            LOG.warn("Не вдалося видалити рахунок #{}: {}", accountId, e.getMessage());
        }
    }

    private static void guardAgainstReadOnlyEnvironment() {
        if (ConfigManager.getBool("env.readonly", false)) {
            throw new SkipException("Середовище '" + ConfigManager.env()
                    + "' позначене як read-only: тести, що змінюють дані, не запускаються");
        }
    }
}
