package com.example.projectdnd.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service responsible for generating, signing, parsing, and validating JSON Web Tokens (JWT)
 * using HMAC-SHA256.
 */
@Service
public class JwtService {

    // Injected securely from application.properties / environment variable
    @Value("${jwt.secret}")
    private String secretKey;

    // Token validity duration: 24 hours in milliseconds
    private static final long EXPIRATION_TIME_MS = 1000 * 60 * 60 * 24;

    /**
     * Derives the cryptographic HMAC-SHA signing key from the configured secret string.
     *
     * @return the signing {@link Key}
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Generates a signed JWT token for the specified username with no additional custom claims.
     *
     * @param username the subject username to embed in the token
     * @return the compact serialized JWT string
     */
    public String generateToken(String username) {
        return generateToken(new HashMap<>(), username);
    }

    /**
     * Generates a signed JWT token containing custom claims and the specified subject username,
     * valid for 24 hours.
     *
     * @param extraClaims map of additional claims to include in the JWT payload
     * @param username    the subject username to embed in the token
     * @return the compact serialized JWT string
     */
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the subject (username) from a signed JWT token.
     *
     * @param token the JWT token string
     * @return the username stored in the token's subject claim
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token using the provided resolver function.
     *
     * @param <T>            the type of the claim value
     * @param token          the JWT token string
     * @param claimsResolver function that selects the desired claim from {@link Claims}
     * @return the resolved claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validates that the token belongs to the given username and has not expired.
     *
     * @param token    the JWT token string to validate
     * @param username the expected username to match against the token subject
     * @return {@code true} if the token is valid and unexpired, {@code false} otherwise
     */
    public boolean isTokenValid(String token, String username) {
        final String extractedUser = extractUsername(token);
        return (extractedUser.equals(username) && !isTokenExpired(token));
    }

    /**
     * Checks whether the given JWT token has passed its expiration timestamp.
     *
     * @param token the JWT token string
     * @return {@code true} if the token's expiration date is before the current time
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration timestamp from the given JWT token.
     *
     * @param token the JWT token string
     * @return the {@link Date} when the token expires
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parses the JWT token, verifies its signature using the signing key, and returns all claims.
     *
     * @param token the JWT token string
     * @return the parsed {@link Claims} body
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}