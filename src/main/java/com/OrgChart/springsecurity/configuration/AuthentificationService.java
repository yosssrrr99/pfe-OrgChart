package com.OrgChart.springsecurity.configuration;


import com.OrgChart.springsecurity.configuration.JwtService;
import com.OrgChart.springsecurity.dto.AuthenticationRequest;
import com.OrgChart.springsecurity.dto.AuthenticationResponse;
import com.OrgChart.springsecurity.dto.RegisterRequest;
import com.OrgChart.springsecurity.entities.Role;
import com.OrgChart.springsecurity.entities.User;
import com.OrgChart.springsecurity.repositories.UserRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthentificationService {

    private final UserRepositories userRepositories;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .mailAddress(request.getMailAddress())
                .image(request.getImage())
                .grade(request.getGrade())
                .qualifications(request.getQualifications())
                .DateEmployment(request.getDateEmployment())
                .password(passwordEncoder.encode(request.getPassword()))
                .poste(request.getPoste())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .nomorg(request.getNomorg())
                .build();
        userRepositories.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Authentifie l'utilisateur avec le nom d'utilisateur et le mot de passe
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Récupère l'utilisateur depuis le dépôt
        var user = userRepositories.findByMailAddress(request.getUsername())
                .orElseThrow();

        // Génère un token JWT
        var jwtToken = jwtService.generateToken(user);

        // Retourne le token et le rôle de l'utilisateur
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name()) // Récupère le rôle en tant que chaîne de caractères
                .build();
    }




    public List<User> getAllUsers() {
        return userRepositories.findAll();
    }

}