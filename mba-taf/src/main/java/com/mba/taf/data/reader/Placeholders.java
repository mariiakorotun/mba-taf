package com.mba.taf.data.reader;

import com.mba.taf.core.config.ConfigManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Placeholders {

    private static final Pattern PLACEHOLDER = Pattern.compile("^\\$\\{([a-zA-Z0-9._-]+)}$");

    private Placeholders() {
    }

    public static String resolve(String value) {
        if (value == null) {
            return "";
        }
        Matcher m = PLACEHOLDER.matcher(value);
        return m.matches() ? ConfigManager.get(m.group(1)) : value;
    }
}
