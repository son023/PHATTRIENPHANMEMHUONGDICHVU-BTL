package com.auth_service.auth_service.service;

import com.auth_service.auth_service.client.UserServiceClient;
import com.auth_service.auth_service.exceptions.AppException;
import com.auth_service.auth_service.exceptions.enums.ErrorCode;
import com.auth_service.auth_service.model.User;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    @NonFinal
    @Value("${privateKey}")
    String privateKey;

    UserServiceClient userServiceClient;

    public ResponseEntity<User> login(User user) {
        try {
            User existedUser = userServiceClient.valid(user).getBody();
            String token = this.generate(existedUser);

            return ResponseEntity.ok()
                    .header("Authorization", token)
                    .body(existedUser);

        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    public String generate(User user) {
        try {
            JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.RS256);
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .issuer("dev")
                    .issueTime(new Date())
                    .expirationTime(new Date(
                            Instant.now()
                                    .plus(1, ChronoUnit.DAYS)
                                    .toEpochMilli()
                    ))
                    .subject(user.getUsername())
                    .claim("CURRENT_USER",
                            userServiceClient.getByUsername(user.getUsername()).getBody())
                    .jwtID(UUID.randomUUID().toString())
                    .build();

            SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
            signedJWT.sign(new RSASSASigner(loadPrivateKey()));

            return signedJWT.serialize();
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIED);
        }
    }

    private RSAPrivateKey loadPrivateKey() throws Exception {
        byte[] encoded = Base64.getDecoder().decode(privateKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);

        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

}
