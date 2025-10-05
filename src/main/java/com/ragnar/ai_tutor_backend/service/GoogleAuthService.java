package com.ragnar.ai_tutor_backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.logging.Logger;

/** A service class to handle google Authentication
 * 1. Creates a GoogleIdTokenVerifier
 * 3=2. Verifies the idToken provided by client
 */
@Service // annotation to mark it as a service class which tells the spring boot it is a business logic
//  it also helps to make it as a reusable component
public class GoogleAuthService {
    @Value("${google.client-id}")
    private String clientId;

    // Logger Instance
    private static final Logger LOGGER = Logger.getLogger(GoogleAuthService.class.getName());


    private GoogleIdTokenVerifier verifier; // a helper class to check if the token is signed by google


    /** The main purpose of this method
     * 1. to create a verifier that will verify the idToken sent by client
     * 2. Instead of creating verifier everytime create it once and use it
     * 3. It only creates the verifier if it already does not exist
     */
    public GoogleIdTokenVerifier getVerifier() {
        if (verifier == null) {
            verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singleton(clientId))
                    .build();
        }
        return  verifier;
    }

    /** The main purpose of this method
     * 1. this method is to get client idToken as parameter
     * 2. use the verifier above to verify the token
     * 3. if the token is valid the return the GoogleIdToken
     * 4. Else raise an error as IllegalArgumentException
     */
    public GoogleIdToken verifyIdToken(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = getVerifier().verify(idTokenString);

        if (idToken == null) {
            throw  new IllegalArgumentException("Invalid idToken");
        }
        return idToken;
    }

    /** The main purpose of this method
     * 1. use the verifyTokenMethod to verify the token and return the payload the contains user info
     * such as email, name, user id, profilePicUri, etc
     */
    public GoogleIdToken.Payload extractPayLoad(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = verifyIdToken(idTokenString);
        return  idToken.getPayload();

    }
}
