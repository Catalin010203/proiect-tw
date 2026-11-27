package com.academicevents.controller;

import com.academicevents.dto.AuthResponse;
import com.academicevents.dto.LoginRequest;
import com.academicevents.dto.RegisterRequest;
import com.academicevents.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    public AuthController(AuthService authService,
                          AuthenticationManager authenticationManager,
                          SecurityContextRepository securityContextRepository) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        try {
            authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Contul a fost creat cu succes"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error",  e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest,
                                   HttpServletResponse httpResponse) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.email().trim(), request.password()));
        } catch (AuthenticationException e) {
            // Acelasi mesaj pentru email inexistent si parola gresita (fara user enumeration)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email sau parola incorecte"));
        }

        // Protectie la session fixation: sesiunea veche primeste un id nou dupa login
        if (httpRequest.getSession(false) != null) {
            httpRequest.changeSessionId();
        }

        // Salveaza utilizatorul autentificat in sesiune (cookie JSESSIONID)
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        AuthResponse response = authService.getAuthResponse(authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Frontendul apeleaza acest endpoint o data la incarcarea paginii, ca sa primeasca cookie-ul XSRF-TOKEN.
     * Valoarea cookie-ului se trimite apoi in headerul X-XSRF-TOKEN la POST/PUT/PATCH/DELETE.
     */
    @GetMapping("/csrf")
    public ResponseEntity<Void> csrf(CsrfToken csrfToken) {
        csrfToken.getToken(); // forteaza generarea tokenului si scrierea cookie-ului
        return ResponseEntity.noContent().build();
    }
}
