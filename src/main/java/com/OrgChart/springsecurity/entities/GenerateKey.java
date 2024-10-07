package com.OrgChart.springsecurity.entities;

import io.jsonwebtoken.security.Keys;

public class GenerateKey {

    public static void main(String[] args) {
        // Generate a secure key for HS256
        var key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        String base64Key = java.util.Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Generated Key (Base64): " + base64Key);
    }

}
