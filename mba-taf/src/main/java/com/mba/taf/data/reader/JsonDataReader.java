package com.mba.taf.data.reader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mba.taf.data.model.DataCategory;
import com.mba.taf.data.model.DataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class JsonDataReader {

    private static final Logger LOG = LoggerFactory.getLogger(JsonDataReader.class);
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    private JsonDataReader() {
    }

    public static <T extends Record> List<T> read(String resource, Class<T> type) {
        JavaType dataSetType = MAPPER.getTypeFactory().constructParametricType(DataSet.class, type);
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Файл тестових даних не знайдено: " + resource);
            }
            DataSet<T> dataSet = MAPPER.readValue(in, dataSetType);
            List<T> filtered = dataSet.cases().stream().filter(JsonDataReader::categoryAllowed).toList();
            LOG.info("Зчитано {} з {} наборів даних з {} ({})",
                    filtered.size(), dataSet.cases().size(), resource, dataSet.description());
            return filtered;
        } catch (IOException e) {
            throw new UncheckedIOException("Помилка розбору " + resource, e);
        }
    }

    public static <T extends Record> Object[][] asDataProvider(String resource, Class<T> type) {
        return read(resource, type).stream().map(c -> new Object[]{c}).toArray(Object[][]::new);
    }

    private static boolean categoryAllowed(Record testCase) {
        String filter = System.getProperty("data.categories", "").trim();
        if (filter.isEmpty()) {
            return true;
        }
        Set<String> allowed = Arrays.stream(filter.split(",")).map(String::trim).map(String::toUpperCase)
                .collect(Collectors.toSet());
        for (RecordComponent component : testCase.getClass().getRecordComponents()) {
            if (component.getType() == DataCategory.class) {
                try {
                    Object value = component.getAccessor().invoke(testCase);
                    return value != null && allowed.contains(value.toString());
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return true;
    }
}
