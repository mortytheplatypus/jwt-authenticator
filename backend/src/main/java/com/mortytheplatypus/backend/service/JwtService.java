package com.mortytheplatypus.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mortytheplatypus.backend.model.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Data
@Service
public class JwtService {

    @Value("${application.security.jwt.secret}") 
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long expiration;

    /**
     * Decodes the secret key and returns it as a SecretKey object.
     *
     * @return the SecretKey object used for signing JWTs.
     */
    private @NotNull SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts all claims from the given JWT token.
     *
     * @param token the JWT token from which to extract claims.
     * @return the Claims object containing all the claims.
     */
    private Claims extractAllClaims(String token) {
        return  Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts a specific claim from the given JWT token using the provided resolver function.
     *
     * @param token the JWT token from which to extract the claim.
     * @param resolver the function to extract a specific claim.
     * @param <T> the type of the claim to be extracted.
     * @return the extracted claim.
     */
    public <T> T extractClaim(String token, @NotNull Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    /**
     * Extracts the expiration date from the given JWT token.
     *
     * @param token the JWT token from which to extract the expiration date.
     * @return the expiration date of the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Checks if the given JWT token has expired.
     *
     * @param token the JWT token to check.
     * @return true if the token has expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before((new Date()));
    }

    /**
     * Extracts the username (subject) from the given JWT token.
     *
     * @param token the JWT token from which to extract the username.
     * @return the username contained in the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Validates the given JWT token by checking if it has expired and if the username matches the given UserDetails.
     *
     * @param token the JWT token to validate.
     * @param user the UserDetails object to match the username.
     * @return true if the token is valid, false otherwise.
     */
    public boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);

        return !isTokenExpired(token) && (username.equals(user.getUsername()));
    }

    /**
     * Generates a new JWT token for the given user.
     *
     * @param user the user for whom the token is generated.
     * @return the generated JWT token.
     */
    public String generateToken(@NotNull User user) {

        return Jwts
                .builder()
                .subject(user.getUsername())
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .claim("role", user.getRole().name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }
}
