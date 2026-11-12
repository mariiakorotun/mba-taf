package com.mba.taf.data.model;

public record Credentials(String username, String password) {
    @Override
    public String toString() {
        return "Credentials[" + username + ", ******]";
    }
}
