package com.mba.taf.business.api;

import com.mba.taf.core.config.ConfigManager;
import com.mba.taf.core.http.HttpClientFactory;
import com.mba.taf.core.util.MoneyParser;
import com.mba.taf.data.model.Credentials;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public class BankApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(BankApiClient.class);

    private final RequestSpecification baseSpec = HttpClientFactory.baseSpec();
    private final CookieFilter cookies = new CookieFilter();
    private final String baseUrl = ConfigManager.get("base.url");
    private String currentUser = "anonymous";

    public static BankApiClient anonymous() {
        return new BankApiClient();
    }

    public static BankApiClient loggedInAs(Credentials credentials) {
        BankApiClient client = new BankApiClient();
        if (!client.login(credentials.username(), credentials.password())) {
            throw new IllegalStateException("Не вдалося увійти як '" + credentials.username()
                    + "'. Перевірте облікові дані в конфігурації середовища " + ConfigManager.env());
        }
        return client;
    }

    @Step("API: вхід як «{username}»")
    public boolean login(String username, String password) {
        String csrf = csrfTokenFrom("/login");
        Response response = request()
                .noFiltersOfType(AllureRestAssured.class)   // пароль не потрапляє у вкладення звіту
                .formParam("username", username)
                .formParam("password", password)
                .formParam("_csrf", csrf)
                .post("/login");
        String location = String.valueOf(response.getHeader("Location"));
        boolean success = response.statusCode() == 302 && location.contains("/dashboard");
        if (success) {
            currentUser = username;
        }
        LOG.info("Вхід як '{}': {}", username, success ? "успішно" : "відмова (" + location + ")");
        return success;
    }

    @Step("API: вихід із системи")
    public void logout() {
        String csrf = csrfTokenFrom("/dashboard");
        request().formParam("_csrf", csrf).post("/logout");
        currentUser = "anonymous";
    }

    @Step("API: GET {path}")
    public Response get(String path) {
        return request().get(path);
    }

    @Step("API: отримати список рахунків поточного користувача")
    public List<AccountView> accounts() {
        return getPage("/accounts").select(".account-card").stream().map(BankApiClient::toAccount).toList();
    }

    public Optional<AccountView> account(long accountId) {
        return accounts().stream().filter(a -> a.id() == accountId).findFirst();
    }

    @Step("API: поповнити рахунок #{accountId} на «{amount}»")
    public OperationResult deposit(long accountId, String amount) {
        return accountOperation(accountId, "deposit", amount);
    }

    @Step("API: зняти з рахунку #{accountId} суму «{amount}»")
    public OperationResult withdraw(long accountId, String amount) {
        return accountOperation(accountId, "withdraw", amount);
    }

    @Step("API (admin): знайти ID користувача «{username}»")
    public long findUserId(String username) {
        for (Element row : getPage("/admin").select("table tbody tr")) {
            Element name = row.selectFirst("td:nth-child(1) span.fw-bold");
            Element idInput = row.selectFirst("input[name=userId]");
            if (name != null && idInput != null && name.text().trim().equals(username)) {
                return Long.parseLong(idInput.attr("value"));
            }
        }
        throw new IllegalStateException("Користувача '" + username + "' не знайдено в адмін-панелі");
    }

    @Step("API (admin): створити рахунок {currency} для користувача ID={userId}")
    public OperationResult createAccount(long userId, String currency) {
        String csrf = csrfTokenFrom("/admin");
        Response response = request()
                .formParam("userId", userId)
                .formParam("currency", currency)
                .formParam("_csrf", csrf)
                .post("/admin/accounts/create");
        return followRedirect(response);
    }

    @Step("API (admin): видалити рахунок #{accountId}")
    public int deleteAccount(long accountId) {
        String csrf = csrfTokenFrom("/dashboard");
        return request()
                .formParam("accountId", accountId)
                .formParam("_csrf", csrf)
                .post("/accounts/delete-direct")
                .statusCode();
    }

    private RequestSpecification request() {
        return RestAssured.given().spec(baseSpec).filter(cookies).redirects().follow(false);
    }

    private OperationResult accountOperation(long accountId, String operation, String amount) {
        String csrf = csrfTokenFrom("/accounts");
        Response response = request()
                .formParam("amount", amount)
                .formParam("_csrf", csrf)
                .post("/accounts/{id}/" + operation, accountId);
        return followRedirect(response);
    }

    private OperationResult followRedirect(Response postResponse) {
        int status = postResponse.statusCode();
        if (status != 302) {
            return new OperationResult(status, "", "", null);
        }
        String path = toPath(postResponse.getHeader("Location"));
        Document page = getPage(path);
        return new OperationResult(status, textOf(page, ".alert-success"), textOf(page, ".alert-danger"), path);
    }

    private Document getPage(String path) {
        Response response = request().get(path);
        if (response.statusCode() != 200) {
            throw new IllegalStateException("GET " + path + " повернув " + response.statusCode()
                    + " для користувача '" + currentUser + "'");
        }
        return Jsoup.parse(response.asString(), baseUrl);
    }

    private String csrfTokenFrom(String path) {
        Element token = getPage(path).selectFirst("input[name=_csrf]");
        if (token == null) {
            throw new IllegalStateException("CSRF-токен не знайдено на сторінці " + path);
        }
        return token.attr("value");
    }

    private static String toPath(String location) {
        URI uri = URI.create(location);
        String path = uri.getRawPath();
        return uri.getRawQuery() != null ? path + "?" + uri.getRawQuery() : path;
    }

    private static String textOf(Document page, String css) {
        Element element = page.selectFirst(css);
        return element != null ? element.text().trim() : "";
    }

    private static AccountView toAccount(Element card) {
        String idText = card.select("span.small").stream()
                .map(Element::text)
                .filter(t -> t.startsWith("ID:"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("На картці рахунку немає ID"));
        long id = Long.parseLong(idText.replace("ID:", "").trim());
        Element title = card.selectFirst("h5.card-title");
        Elements balanceSpans = card.select(".balance-value > span");
        return new AccountView(id,
                title != null ? title.text().trim() : "",
                MoneyParser.parse(balanceSpans.get(0).text()),
                balanceSpans.get(1).text().trim());
    }
}
