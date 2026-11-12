package com.mba.taf.data.model;

import com.mba.taf.data.reader.Placeholders;

public record LoginCase(String id,
                        DataCategory category,
                        String username,
                        String password,
                        ExpectedOutcome expected,
                        String expectedText,
                        String description) {

    public String resolvedUsername() {
        return Placeholders.resolve(username);
    }

    public String resolvedPassword() {
        return Placeholders.resolve(password);
    }

    @Override
    public String toString() {
        return id + " " + category + ": " + description;
    }
}
