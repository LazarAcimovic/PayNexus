# Dokumentacija API-ja - SOAS 2024/25
Lazar Aćimović IT 35/2021

## API Gateway URL
Sve funkcionalnosti dostupne preko API Gateway-a na portu 8765:  
`http://localhost:8765`

---

## 1. Users Service

| Endpoint | Method | Autorizacija | Opis |
|----------|--------|--------------|------|
| /users/newAdmin | POST | OWNER | Kreira korisnika sa ulogom ADMIN |
| /users/newUser | POST | OWNER, ADMIN | Kreira korisnika sa ulogom USER |
| /users | GET | OWNER, ADMIN | Prikaz svih korisnika |
| /users | PUT | OWNER, ADMIN | Ažuriranje korisnika |
| /users | DELETE | OWNER | Brisanje korisnika |

---

## 2. Bank Account Service

| Endpoint | Method | Autorizacija | Opis |
|----------|--------|--------------|------|
| /bank-accounts/getAllBankAccounts | GET | ADMIN | Prikaz svih bankovnih računa |
| /bank-accounts/email | GET | USER | Prikaz korisnikovog bankovnog računa |
| /bank-accounts/delete | DELETE | OWNER | Automatsko brisanje korisnikovog računa (kada se obriše korisnik) |
| /bank-accounts/new | POST | ADMIN | Dodavanje bankovnog računa |
| /bank-accounts/update | PUT | ADMIN | Ažuriranje bankovnog računa |
| //bank-accounts/update/user | PUT | USER | Ažuriranje bankovnog računa (automatski se poziva kod trade service |



---

## 3. Currency Exchange Service

| Endpoint | Method | Autorizacija | Opis |
|----------|--------|--------------|------|
| /currency-exchange | GET | Svi | Prikaz kurseva valuta |

---

## 4. Currency Conversion Service

| Endpoint | Method | Autorizacija | Opis |
|----------|--------|--------------|------|
| /currency-conversion?from=X&to=Y&quantity=Q | GET | USER | Razmena fiat valuta |

---

## 5. Crypto Wallet Service

| Endpoint | Method | Autorizacija | Opis |
|----------|--------|--------------|------|
| /crypto-wallets/new | POST | ADMIN | Kreiranje novčanika za korisnika |
| /crypto-wallets/update | PUT | ADMIN | Ažuriranje novčanika |
| /crypto-wallets/delete | DELETE | OWNER | Brisanje novčanika |
| /crypto-wallets/all | GET | ADMIN | Prikaz svih novčanika |
| /crypto-wallets/email | GET | USER | Prikaz korisnikovog novčanika |

---

## 6. Crypto Exchange Service

| Endpoint | Method | Autorizacija | Opis |
|----------|--------|--------------|------|
| /crypto-exchange | GET | Svi | Prikaz kurseva crypto valuta |

---

## 7. Crypto Conversion Service

| Endpoint | Method | Autorizacija | Opis |
|----------|--------|--------------|------|
| /crypto-conversion?from=X&to=Y&quantity=Q | GET | USER | Razmena crypto valuta |

---

## 8. Trade Service

| Endpoint | Method | Autorizacija | Opis |
|----------|--------|--------------|------|
| /trade-service?from=X&to=Y&quantity=Q | GET | USER | Razmena fiat i crypto valuta |

---

## Kredencijali korisnika

| Email | Lozinka | Uloga |
|-------|---------|-------|
| admin@uns.ac.rs | adminPassword | ADMIN |
| owner@uns.ac.rs | ownerPassword | OWNER |
| user@uns.ac.rs | userPassword | USER |




