package com.ragnar.ai_tutor_backend.model;

public class GoogleSignInRequest {
    private String idToken;

    public GoogleSignInRequest(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
    @Override
    public String toString() {
        return "GoogleSignInRequest{" +
                "idToken='" + idToken + '\'' +
                '}';
    }
}
