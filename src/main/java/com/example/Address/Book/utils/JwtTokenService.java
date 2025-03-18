package com.example.Address.Book.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtTokenService {

    private static final String TOKEN_SECRET = "Lock";


    public String createToken(Long id)   {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);

            long expirationMillis = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);

            String token = JWT.create()
                    .withClaim("user_id", id)
                    .withExpiresAt(new Date(expirationMillis))
                    .sign(algorithm);

            return token;

        } catch (JWTCreationException exception) {
            exception.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Long decodeToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("user_id").asLong();
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token.");
        }
    }

}