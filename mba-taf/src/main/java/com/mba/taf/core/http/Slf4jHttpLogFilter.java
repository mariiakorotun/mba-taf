package com.mba.taf.core.http;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Slf4jHttpLogFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger("HTTP");
    private static final Set<String> SECRET_PARAMS = Set.of("password", "_csrf");

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        long start = System.nanoTime();
        Response response = ctx.next(request, responseSpec);
        long ms = (System.nanoTime() - start) / 1_000_000;

        String location = response.getHeader("Location");
        LOG.info("{} {} -> {}{} ({} ms)",
                request.getMethod(),
                request.getURI(),
                response.getStatusCode(),
                location != null ? " Location: " + location : "",
                ms);
        if (!request.getFormParams().isEmpty()) {
            LOG.debug("   form: {}", mask(request.getFormParams()));
        }
        if (response.getStatusCode() >= 500) {
            LOG.error("   Серверна помилка, фрагмент тіла: {}", abbreviate(response.asString()));
        }
        return response;
    }

    private static Map<String, String> mask(Map<String, String> params) {
        Map<String, String> masked = new TreeMap<>(params);
        masked.replaceAll((k, v) -> SECRET_PARAMS.contains(k) ? "******" : v);
        return masked;
    }

    private static String abbreviate(String body) {
        String oneLine = body.replaceAll("\\s+", " ");
        return oneLine.length() > 300 ? oneLine.substring(0, 300) + "…" : oneLine;
    }
}
