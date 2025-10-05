package com.ragnar.ai_tutor_backend.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.ragnar.ai_tutor_backend.model.AuthResponse;
import com.ragnar.ai_tutor_backend.model.GoogleSignInRequest;
import com.ragnar.ai_tutor_backend.model.User;
import com.ragnar.ai_tutor_backend.service.GoogleAuthService;

import com.ragnar.ai_tutor_backend.service.UserService;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.logging.Logger;

@RestController  // marks it as rest controller class
@RequestMapping("/auth") // base url for this controller
public class AuthController {

    @Autowired
    private GoogleAuthService googleAuthService;
    @Autowired
    private UserService userService;



    // Logger Instance
    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());

    // for Google sign-in and sign-up
    @PostMapping("/google-sign-in")
    public ResponseEntity<?> googleSignIn(@RequestBody GoogleSignInRequest request) {
        LOGGER.info("Received payload from client: " + request.toString());
        String idToken = request.getIdToken();

        if (idToken == null) {
            return ResponseEntity.badRequest().body("Missing idToken in request body");
        }
        try {
            GoogleIdToken.Payload payload = googleAuthService.extractPayLoad(idToken);

            // User information extracted from payload
            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String photoUrl = (String) payload.get("picture");

            User user = new User(googleId, email, name, photoUrl); // creates a new User object
            User savedUser = userService.saveOrUpdateUser(user); // Service class checks if user exists if exists then update else save new user

            // create a response message
            AuthResponse response = new AuthResponse("token", savedUser, 3600);

            return  new ResponseEntity<>(response, HttpStatus.OK);

        }
        catch (GeneralSecurityException | IOException e) {
            LOGGER.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Google Id Token" + e);

        }
        catch (Exception e) {
            LOGGER.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Authentication Failed: " +e);

        }
    }

}
