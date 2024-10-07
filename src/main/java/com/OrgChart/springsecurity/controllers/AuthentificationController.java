package com.OrgChart.springsecurity.controllers;

import com.OrgChart.springsecurity.configuration.JwtService;
import com.OrgChart.springsecurity.dto.AuthenticationRequest;
import com.OrgChart.springsecurity.dto.AuthenticationResponse;
import com.OrgChart.springsecurity.dto.RegisterRequest;
import com.OrgChart.springsecurity.configuration.AuthentificationService;
import com.OrgChart.springsecurity.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthentificationController {

    private final AuthentificationService authentificationService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        authentificationService.register(request);
       return ResponseEntity.ok(request);
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return  ResponseEntity.ok(authentificationService.authenticate(request));
    }

    @GetMapping("/role")
    public ResponseEntity<Map<String, String>> getRole(@RequestHeader("Authorization") String token) {
        // Assurez-vous que le token commence par "Bearer "
        String jwtToken = token.replace("Bearer ", "");
        String role = jwtService.extractRole(jwtToken);
        System.out.println("role: " + role);

        Map<String, String> response = new HashMap<>();
        response.put("role", role);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/is-connected")
    public ResponseEntity<Boolean> isConnected(@RequestHeader("Authorization") String token) {
        // Assurez-vous que le token commence par "Bearer "
        String jwtToken = token.replace("Bearer ", "");

        // Récupérer l'objet Authentication actuel
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Vérifier si l'authentication est non nulle et obtenir les détails de l'utilisateur
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Vérifiez si userDetails est non nul avant de passer à la méthode isTokenValid
        boolean isConnected = userDetails != null && jwtService.isTokenValid(jwtToken, userDetails);

        return ResponseEntity.ok(isConnected);
    }

    @GetMapping("/is-connected-id")
    public ResponseEntity<String> isConnectedId(@RequestHeader("Authorization") String token) {
        // Assurez-vous que le token commence par "Bearer "
        String jwtToken = token.replace("Bearer ", "");

        // Récupérer l'objet Authentication actuel
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Vérifier si l'authentication est non nulle et obtenir les détails de l'utilisateur
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Vérifiez si le token est valide
            boolean isTokenValid = jwtService.isTokenValid(jwtToken, userDetails);

            if (isTokenValid) {
                // Si le token est valide, cast vers votre classe User pour accéder à l'ID
                User user = (User) userDetails;
                return ResponseEntity.ok(user.getFirstName());
            }
        }

        // Si l'authentication est nulle ou le token invalide, retourner une réponse non autorisée
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return authentificationService.getAllUsers();
    }
}
