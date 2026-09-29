package com.trustlend.api.security;

import java.util.UUID;

public record ActorIdentity(UUID userId) {
    public ActorIdentity {
        if (userId == null) throw new IllegalArgumentException("Actor user ID is required");
    }
}
