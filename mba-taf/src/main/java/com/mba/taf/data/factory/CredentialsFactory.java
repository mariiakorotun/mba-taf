package com.mba.taf.data.factory;

import com.mba.taf.core.config.ConfigManager;
import com.mba.taf.data.model.Credentials;

import java.util.UUID;

public final class CredentialsFactory {

    private CredentialsFactory() {
    }

    public static Credentials user() {
        return new Credentials(ConfigManager.get("user.username"), ConfigManager.get("user.password"));
    }

    public static Credentials admin() {
        return new Credentials(ConfigManager.get("admin.username"), ConfigManager.get("admin.password"));
    }

    public static Credentials unknownUser() {
        return new Credentials("ghost_" + UUID.randomUUID().toString().substring(0, 8), "Wr0ng#Pass");
    }
}
