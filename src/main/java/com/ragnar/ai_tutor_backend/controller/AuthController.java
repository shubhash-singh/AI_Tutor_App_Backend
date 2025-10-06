package com.ragnar.ai_tutor_backend.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.ragnar.ai_tutor_backend.model.AuthResponse;
import com.ragnar.ai_tutor_backend.model.GoogleSignInRequest;
import com.ragnar.ai_tutor_backend.model.User;
import com.ragnar.ai_tutor_backend.service.GoogleAuthService;

import com.ragnar.ai_tutor_backend.service.JWTService;
import com.ragnar.ai_tutor_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;

@RestController  // marks it as rest controller class
@RequestMapping("/auth") // base url for this controller
public class AuthController {

    @Autowired
    private GoogleAuthService googleAuthService;
    @Autowired
    private UserService userService;
    @Autowired
    private JWTService jwtService;

    @Value("${google.client-id}")
    private String clientId;
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

            String token = jwtService.generateToken(savedUser.getId());
            // create a response message
            AuthResponse response = new AuthResponse(token, savedUser, jwtService.getExpiration());

            return  new ResponseEntity<>(response, HttpStatus.OK);

        }
        catch (GeneralSecurityException | IOException e) {
            LOGGER.info(e.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Google Id Token" + e);

        }
        catch (Exception e) {
            LOGGER.info(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Authentication Failed: " +e);

        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                LOGGER.info("Invalid Authorization Header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid Authorization Header");
            }

            String token = authHeader.substring(7); // initial 7 characters are Bearer and 1 space i.e. 0-6

            if (jwtService.validateToken(token)) {
                String userId = jwtService.extractUserId(token);
                LOGGER.info("Token is valid for user: "+ userId);
                return ResponseEntity.ok("Token is valid for user: "+ userId);
            }
            else {
                LOGGER.info("Invalid or Expired Token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or Expired Token");
            }
        } catch (Exception e) {
            LOGGER.info(e.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Validation failed. \n Message: "+ e.getMessage());
        }
    }
}
