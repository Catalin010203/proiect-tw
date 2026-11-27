package com.academicevents.service;

import com.academicevents.dto.AuthResponse;
import com.academicevents.dto.RegisterRequest;
import com.academicevents.model.User;
import com.academicevents.model.UserRole;
import com.academicevents.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public AuthService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = encoder;
    }

    @Transactional
    public void register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if(userRepository.existsByEmailIgnoreCase(email)){
            throw new IllegalArgumentException("Adresa de email este deja folosita");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(
                request.firstName().trim(),
                request.lastName().trim(),
                email,
                passwordHash,
                UserRole.USER
        );

        userRepository.save(user);
    }

    /** Datele trimise frontendului dupa login (fara parola). */
    public AuthResponse getAuthResponse(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalStateException("Utilizatorul autentificat nu a fost gasit"));

        return new AuthResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
