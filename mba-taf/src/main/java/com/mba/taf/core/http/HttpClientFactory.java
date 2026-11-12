package com.mba.taf.core.http;

import com.mba.taf.core.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;

public final class HttpClientFactory {

    private HttpClientFactory() {
    }

    public static RequestSpecification baseSpec() {
        int timeoutMs = ConfigManager.getInt("timeout.http.ms", 15000);
        RestAssuredConfig config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", timeoutMs)
                        .setParam("http.socket.timeout", timeoutMs))
                .encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"));

        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.get("base.url"))
                .setConfig(config)
                .addFilter(new Slf4jHttpLogFilter())
                .addFilter(new AllureRestAssured())
                .build();
    }
}
