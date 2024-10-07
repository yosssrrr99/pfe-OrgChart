package com.hracces.openhr.Controllers;

import com.hracces.openhr.dto.AuthenticationRequest;
import com.hracces.openhr.dto.AuthentificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("")
public class CommunicationController {
    @Autowired
    private RestTemplate restTemplate;

    private static final String AUTH_SERVICE_URL = "http://localhost:9095/api/v1/auth/authenticate";

    @PostMapping("/login")
    public ResponseEntity<AuthentificationResponse> login(@RequestBody AuthenticationRequest request) {
        // Appel à l'API du service d'authentification
        AuthentificationResponse response = restTemplate.postForObject(AUTH_SERVICE_URL, request, AuthentificationResponse.class);

        // Retourner la réponse obtenue du service d'authentification
        return ResponseEntity.ok(response);
    }
    @GetMapping("/login-role")
    public ResponseEntity<String> getRoleFromLogin(AuthenticationRequest request) {
        // Appel à l'API du service d'authentification
        AuthentificationResponse response = restTemplate.postForObject(AUTH_SERVICE_URL, request, AuthentificationResponse.class);

        // Vérifiez si la réponse est non nulle et contient un rôle
        if (response != null && response.getRole() != null) {
            return ResponseEntity.ok(response.getRole());
        }

        // Retourner une réponse avec un message d'erreur si le rôle n'est pas disponible
        return ResponseEntity.badRequest().body("Role not found");
    }


}
