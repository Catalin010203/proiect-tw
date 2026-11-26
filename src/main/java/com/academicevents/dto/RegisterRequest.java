package com.academicevents.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest (

        @NotBlank(message = "Prenumele este obligatoriu")
        @Size(min = 2, max = 80, message = "Prenumele poate avea intre 2 si 80 de caractere")
        String firstName,

        @NotBlank(message = "Numele este obligatoriu")
        @Size(min = 2, max = 80, message = "Numele poate avea intre 2 si 80 de caractere")
        String lastName,

        @NotBlank(message = "Emailul este obligatoriu")
        @Email(message = "Adresa de email nu este valida")
        @Size(max = 254, message = "Emailul poate avea cel mult 254 de caractere")
        String email,

        @NotBlank(message = "Parola este obligatorie")
        @Size(min = 6, max = 72, message = "Parola trebuie sa aiba intre 6 si 72 caractere")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>_\\-+=\\[\\]\\\\/;'`~]).+$",
                message = "Parola trebuie sa contina o litera mare, o cifra si un caracter special"
        )
        String password
) {

}

