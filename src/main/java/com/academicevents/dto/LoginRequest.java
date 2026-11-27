package com.academicevents.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest (

    @NotBlank(message = "Emailul este obligatoriu")
    @Email(message = "Adresa de email nu este valida")
    @Size(max = 254)
    String email,

    @NotBlank
    @Size(min = 6, max = 72, message = "Parola trebuie sa contina intre 6 si 72 de caractere")
    String password
) {
}
