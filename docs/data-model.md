
# Data Model - Academic Events

## 1. Descriere generala

Baza de date a aplicatiei va fi realizata folosind MySQL. Aceasta va stoca informatiile despre utilizatori, evenimente, 
inscrieri, agenda si feedback.

## 2. Entitati principale

### User
Retine informatiile utilizatorilor aplicatiei.
- id (PK)
- first_name
- last_name
- email
- password
- role (ADMIN, ORGANIZER, PARTICIPANT)

### Event
Retine informatiile despre evenimentele academice.
- id (PK)
- title
- description
- location
- start_date
- end_date
- organizer_id (FK -> User.id)

### Registration
Retine inscrierile participantilor la evenimente.
- id (PK)
- user_id (FK -> User.id)
- event_id (FK -> Event.id)
- registration_date
- status

### Agenda
Retine activitatile si sesiunile din cadrul evenimentelor.
- id (PK)
- event_id (FK -> Event.id)
- title
- description
- start_time
- end_time

### Feedback
Retine evaluarile si opiniile participantilor.
- id (PK)
- user_id (FK -> User.id)
- event_id (FK -> Event.id)
- rating
- comment
- submission_date

## 3. Relatiile dintre entitati

- Un utilizator poate organiza mai multe evenimente.
- Un eveniment poate avea un singur organizator.
- Un utilizator se poate inscrie la mai multe evenimente.
- Un eveniment poate avea mai multi participanti.
- O inscriere apartine unui singur utilizator si unui singur eveniment.
- Un eveniment poate contine mai multe activitati in agenda.
- Un participant poate oferi feedback pentru un eveniment.

## 4. Tipuri de relatii

- User - Event: One-to-Many.
- User - Registration: One-to-Many.
- Event - Registration: One-to-Many.
- Event - Agenda: One-to-Many.
- User - Feedback: One-to-Many.
- Event - Feedback: One-to-Many.

## 5. Constrangeri

- Fiecare utilizator are un identificator unic.
- Adresa de email trebuie sa fie unica.
- Un participant nu se poate inscrie de mai multe ori la acelasi eveniment.
- Ratingul feedbackului trebuie sa fie intr-un interval de la 1 la 5.
- Datele obligatorii trebuie completate inainte de salvare.

## 6. Endpoint-uri propuse

Backendul Spring Boot va expune un REST API sub prefixul `/api`. Datele sunt transmise in format JSON, cu exceptia
exportului si importului CSV. Autentificarea se face prin sesiune cu cookie `HttpOnly`, gestionata de Spring Security,
iar accesul la fiecare endpoint este verificat pe server in functie de rolul utilizatorului.

Valorile din coloana **Acces**:
- **Public** - accesibil fara autentificare.
- **Autentificat** - orice utilizator conectat, indiferent de rol.
- **Organizator (propriu)** - organizatorul care a creat evenimentul respectiv.
- **Admin** - doar administratorul.

### 6.1 Autentificare si profil

| Metoda | URL | Acces | Descriere |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Creeaza un cont nou cu rolul PARTICIPANT. Primeste prenume, nume, email si parola. Rolul nu poate fi ales la inregistrare. |
| POST | `/api/auth/login` | Public | Verifica emailul si parola si creeaza sesiunea. Returneaza datele utilizatorului si rolul acestuia. |
| POST | `/api/auth/logout` | Autentificat | Inchide sesiunea curenta. |
| GET | `/api/auth/me` | Autentificat | Returneaza utilizatorul conectat. Este folosit de frontend pentru a afisa meniul potrivit rolului. |
| PUT | `/api/users/me` | Autentificat | Actualizeaza datele din profil (nume, prenume, parola). |

### 6.2 Evenimente

| Metoda | URL | Acces | Descriere |
|---|---|---|---|
| GET | `/api/events` | Public | Returneaza lista evenimentelor. Accepta parametrii optionali `search`, `category` si `sort` (ex. `/api/events?search=java&category=WORKSHOP&sort=date`). |
| GET | `/api/events/{id}` | Public | Returneaza detaliile unui eveniment, inclusiv organizatorul si agenda. |
| GET | `/api/events/mine` | Organizator | Returneaza evenimentele create de organizatorul conectat. |
| POST | `/api/events` | Organizator, Admin | Creeaza un eveniment nou. Organizatorul este setat automat ca utilizatorul conectat. |
| PUT | `/api/events/{id}` | Organizator (propriu), Admin | Modifica datele unui eveniment. |
| DELETE | `/api/events/{id}` | Organizator (propriu), Admin | Sterge un eveniment, impreuna cu agenda si inscrierile asociate. |
| POST | `/api/events/import` | Organizator, Admin | Importa mai multe evenimente dintr-un fisier CSV. Fiecare rand este validat inainte de salvare. |

### 6.3 Inscrieri

| Metoda | URL | Acces | Descriere |
|---|---|---|---|
| POST | `/api/events/{id}/registrations` | Autentificat | Inscrie utilizatorul conectat la eveniment. Returneaza eroare daca este deja inscris. |
| DELETE | `/api/events/{id}/registrations/me` | Autentificat | Retrage inscrierea utilizatorului conectat (statusul devine CANCELLED). |
| GET | `/api/registrations/me` | Autentificat | Returneaza evenimentele la care este inscris utilizatorul conectat (sectiunea „My events”). |
| GET | `/api/events/{id}/registrations` | Organizator (propriu), Admin | Returneaza lista participantilor inscrisi la eveniment. |
| PATCH | `/api/registrations/{id}` | Organizator (propriu), Admin | Modifica statusul unei inscrieri (ex. confirmare sau anulare). |
| GET | `/api/events/{id}/registrations/export` | Organizator (propriu), Admin | Descarca lista participantilor in format CSV. |

### 6.4 Agenda si feedback

| Metoda | URL | Acces | Descriere |
|---|---|---|---|
| GET | `/api/events/{id}/agenda` | Public | Returneaza activitatile din agenda evenimentului, ordonate dupa ora de inceput. |
| POST | `/api/events/{id}/agenda` | Organizator (propriu), Admin | Adauga o activitate in agenda. |
| PUT | `/api/agenda/{id}` | Organizator (propriu), Admin | Modifica o activitate din agenda. |
| DELETE | `/api/agenda/{id}` | Organizator (propriu), Admin | Sterge o activitate din agenda. |
| POST | `/api/events/{id}/feedback` | Autentificat | Trimite feedback (rating 1-5 si comentariu). Permis doar participantilor inscrisi, dupa incheierea evenimentului, o singura data. |
| GET | `/api/events/{id}/feedback` | Organizator (propriu), Admin | Returneaza feedbackul primit pentru eveniment. |

### 6.5 Administrare

| Metoda | URL | Acces | Descriere |
|---|---|---|---|
| GET | `/api/admin/users` | Admin | Returneaza lista utilizatorilor, cu cautare optionala dupa nume sau email. |
| GET | `/api/admin/users/{id}` | Admin | Returneaza detaliile unui utilizator. |
| PUT | `/api/admin/users/{id}/role` | Admin | Schimba rolul unui utilizator (ADMIN, ORGANIZER, PARTICIPANT). Adminul nu isi poate modifica propriul rol. |
| DELETE | `/api/admin/users/{id}` | Admin | Sterge contul unui utilizator. Adminul nu isi poate sterge propriul cont. |

### 6.6 Coduri de raspuns si reguli comune

| Cod | Semnificatie |
|---|---|
| 200 OK | Cererea a reusit. |
| 201 Created | Resursa a fost creata. |
| 204 No Content | Cererea a reusit, fara continut returnat (ex. logout, stergere). |
| 400 Bad Request | Datele trimise nu sunt valide. |
| 401 Unauthorized | Utilizatorul nu este autentificat sau datele de login sunt gresite. |
| 403 Forbidden | Utilizatorul este autentificat, dar nu are dreptul sa faca actiunea. |
| 404 Not Found | Resursa ceruta nu exista. |
| 409 Conflict | Actiunea intra in conflict cu datele existente (ex. email deja folosit, inscriere dubla). |

Reguli aplicate tuturor endpoint-urilor:
- Datele primite sunt validate pe server, chiar daca au fost validate si in frontend.
- Parola este salvata doar sub forma de hash (BCrypt) si nu este returnata de niciun endpoint.
- La login, mesajul de eroare este acelasi pentru email gresit si parola gresita.
- Pentru actiunile marcate „Organizator (propriu)”, serverul verifica daca evenimentul apartine utilizatorului conectat.
- Accesul la baza de date se face prin Spring Data JPA, cu interogari parametrizate, pentru a preveni SQL injection.
