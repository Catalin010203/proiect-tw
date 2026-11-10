
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
