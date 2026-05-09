package lk.ijse.cmjd112.FoodOrderingSystem.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service responsible for JWT token generation, validation, and claim extraction.

 * This service handles:
 *     JWT token creation
 *     Username extraction from tokens
 *     Token validation
 *     JWT signature verification
 *     Token expiration handling

 * JWT tokens are signed using the application's configured secret key.
 */
@Service
public class JwtService {


    // secret loading from application configuration.
    @Value("${app.jwt.secret}")
    private String secret;

    //  JWT token expiration time in milliseconds.
    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    /**
     * Generates a signed JWT token for the authenticated user.
     */
    public String generateToken(UserDetails userDetails) {

        // Current system time
        Date now = new Date();

        // Calculate token expiration time
        Date expiry = new Date(now.getTime() + expirationMs);

        // Build and sign JWT token
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts username from the JWT token.
     */
    public String extractUsername(String token) {

        return extractClaims(token).getSubject();
    }

    /**
     * Validates a JWT token against authenticated user details.
     */
    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        Claims claims = extractClaims(token);

        return claims.getSubject().equals(userDetails.getUsername())
                && claims.getExpiration().after(new Date());
    }

    /**
     * Extracts all claims from a JWT token.
     */
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Generates the cryptographic signing key used for JWT token signing and validation.
     */
    private Key getSigningKey() {

        byte[] keyBytes;

        try {

            // Attempt Base64 decoding
            keyBytes = Decoders.BASE64.decode(secret);

        } catch (IllegalArgumentException ex) {

            // Fallback to raw secret bytes
            keyBytes = secret.getBytes();
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}