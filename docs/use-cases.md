
# Use Cases - Academic Events

Fiecare scenariu contine preconditiile, pasii principali, fluxurile de eroare si criteriile de acceptare prin care se
poate verifica functionalitatea. Endpoint-urile mentionate sunt descrise in [data-model.md](data-model.md#6-endpoint-uri-propuse).

## UC1. Inregistrarea si autentificarea

**Actor:** Vizitator / Utilizator inregistrat

**Preconditii:** Utilizatorul nu este autentificat.

**Flux principal:**
1. Vizitatorul completeaza formularul de inregistrare (prenume, nume, email, parola).
2. Sistemul valideaza datele si creeaza contul cu rolul PARTICIPANT. Parola este salvata ca hash BCrypt.
3. Utilizatorul se autentifica cu emailul si parola.
4. Sistemul creeaza sesiunea si il redirectioneaza catre *Dashboard*.

**Fluxuri de eroare:**
- **2a.** Emailul este deja folosit: sistemul afiseaza un mesaj de eroare, iar contul nu este creat.
- **4a.** Emailul sau parola sunt gresite: sistemul afiseaza „Email sau parola incorecte”, fara a preciza care dintre ele.

**Criterii de acceptare:**
- In tabelul `User` apare un cont nou cu rolul PARTICIPANT, iar parola este salvata doar ca hash.
- O inregistrare cu un email existent returneaza `409`, iar un login gresit returneaza `401`.

**Endpoint-uri:** `POST /api/auth/register`, `POST /api/auth/login`

## UC2. Crearea unui eveniment

**Actor:** Organizator

**Preconditii:** Organizatorul este autentificat si are rolul ORGANIZER.

**Flux principal:**
1. Organizatorul deschide formularul de creare a unui eveniment din *Dashboard*.
2. Completeaza titlul, descrierea, categoria, locatia si datele de inceput si sfarsit.
3. Sistemul valideaza datele si salveaza evenimentul, cu organizatorul conectat.
4. Evenimentul devine vizibil in pagina *Events*.

**Fluxuri de eroare:**
- **3a.** Lipsesc campuri obligatorii sau data de sfarsit este inaintea celei de inceput: evenimentul nu este salvat.
- **1a.** Un participant incearca sa creeze un eveniment: sistemul refuza cererea.

**Criterii de acceptare:**
- In tabelul `Event` apare un rand nou, cu `organizer_id` egal cu id-ul organizatorului conectat.
- O cerere de creare trimisa de un participant returneaza `403`.

**Endpoint-uri:** `POST /api/events`

## UC3. Inscrierea la un eveniment

**Actor:** Participant

**Preconditii:** Participantul este autentificat, iar evenimentul nu a avut loc inca.

**Flux principal:**
1. Participantul cauta un eveniment in pagina *Events* si deschide detaliile acestuia.
2. Apasa butonul de inscriere.
3. Sistemul salveaza inscrierea si afiseaza un mesaj de confirmare.
4. Evenimentul apare in sectiunea *My events*.

**Fluxuri de eroare:**
- **2a.** Participantul nu este autentificat: este redirectionat la pagina de login.
- **3a.** Participantul este deja inscris: sistemul afiseaza un mesaj, iar inscrierea nu se dubleaza.

**Criterii de acceptare:**
- In tabelul `Registration` apare un rand nou cu utilizatorul si evenimentul respectiv.
- O a doua inscriere la acelasi eveniment returneaza `409`.

**Endpoint-uri:** `GET /api/events/{id}`, `POST /api/events/{id}/registrations`

## UC4. Administrarea utilizatorilor

**Actor:** Administrator (Admin)

**Preconditii:** Utilizatorul este autentificat si are rolul ADMIN.

**Flux principal:**
1. Administratorul deschide sectiunea de administrare.
2. Sistemul afiseaza lista utilizatorilor, cu numele, emailul si rolul fiecaruia.
3. Administratorul schimba rolul unui utilizator (de exemplu, din PARTICIPANT in ORGANIZER).
4. Sistemul salveaza noul rol si afiseaza un mesaj de confirmare.

**Fluxuri de eroare:**
- **1a.** Un utilizator fara rolul ADMIN incearca sa acceseze sectiunea: sistemul refuza cererea.
- **3a.** Administratorul incearca sa isi schimbe propriul rol: actiunea este refuzata, pentru ca platforma sa nu ramana fara administrator.

**Criterii de acceptare:**
- Dupa salvare, coloana `role` din tabelul `User` contine noul rol, iar utilizatorul are acces la functionalitatile acestuia.
- Cererile catre `/api/admin/...` trimise de un utilizator fara rolul ADMIN returneaza `403`.

**Endpoint-uri:** `GET /api/admin/users`, `PUT /api/admin/users/{id}/role`
