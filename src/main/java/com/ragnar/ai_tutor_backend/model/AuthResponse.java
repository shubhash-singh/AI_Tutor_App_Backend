package com.ragnar.ai_tutor_backend.model;

/**
 * A custom Response Model class
 */
public class AuthResponse {

    private String token;
    private String tokenType;
    private User user;
    private long expirationIn;

    public AuthResponse(){
    }

    public AuthResponse(String token, User user, long expirationIn){
        this.token = token;
        this.user = user;
        this.expirationIn = expirationIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token){
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpirationIn() {
        return expirationIn;
    }
    public void setExpirationIn(long expirationIn) {
        this.expirationIn = expirationIn;
    }
}
