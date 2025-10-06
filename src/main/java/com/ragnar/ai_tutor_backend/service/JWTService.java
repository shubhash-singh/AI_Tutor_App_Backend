package com.ragnar.ai_tutor_backend.service;

import com.google.api.client.util.Value;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    @Value("${jwt.secret") // gets the value from application.properties
    private String secret;

    @Value("${jwt.expiration:604800}") // 7 days in seconds
    private Long expiration;


    /**
     * THis method ensures that the SecretKey is not just some random string instead a instance of JWT SecretKey
     * @return a JWT Signing KEY using the secret from application.properties
     */
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Creates an empty Map which will contain the additional information
     * Such as Role, email, etc
     * @return a token that is created by createToken method using claims and userID
     */
    public String generateToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        // TODO: Later add more details to this claims Eg. Role, email, etc.
        return createToken(claims, userId);
    }

    /**
     * A method that builds the JWT token using claim and userID
     * @return JWT token as String
     */
    private String createToken(Map<String, Object> claims, String userId) {
        Date date = new Date(); // gets the current time and date
        Date expirationDate = new Date(date.getTime() + expiration * 1000);  // generates the expiration date by adding th e current date and expiration period

        return Jwts.builder() // crete a JWT token builder
                .setClaims(claims) // set JWT payload as claims
                .setSubject(userId) // userId used as subject
                .setIssuedAt(date) // current date as issue date
                .setExpiration(expirationDate) // expiration date
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // use the SHA 256 Hashing algorithm and SigningKey to sign the JWT token
                .compact(); // generates the compact serialized JWT string (three base64url parts joined by dots)
    }

    /**
     * A helper class that extracts the userId from token
     * @return UserId from token
     */
    public String extractUserId(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * A helper class that extracts the expirationDate from token
     *
     * @return UserId from token
     */

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    /**
     * public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) — a generic utility that extracts all claims and then applies the provided function to obtain a specific value of type T.
     * final Claims = extractAllClaims(token); — parses and returns all claims from the token.
     */
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * A helper method that uses the Same signingKey to extract claims from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Signing Key
                .build() // builds a instance of parser
                .parseClaimsJws(token) // parse and validate
                .getBody(); // return the payload
    }

    /**
     * A helper method that takes token as input and check
     * if a token is Expired or not
     * return True if expired else false
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * A method to check if the token is from user with passed user id
     */
    public Boolean validateToken(String token, String userId) {
        final String extractedUserId = extractUserId(token);
        return (extractedUserId.equals(userId) && !isTokenExpired(token));
    }
    /**
     * An override method that only takes token not userId to verify
     * It only checks for Expiration of token
     * it returns false in case of any Exception
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * A helper method to get the Expiration date
     */
    public Long getExpiration() {
        return expiration;
    }
}
