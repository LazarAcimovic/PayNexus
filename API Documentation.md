# API Documentation - SOAS 2024/25
Lazar Aćimović IT 35/2021

## API Gateway URL
All functionalities are available through the API Gateway on port 8765:  
`http://localhost:8765`

---

## 1. Users Service

| Endpoint | Method | Authorization | Description |
|----------|--------|---------------|-------------|
| /users/newAdmin | POST | OWNER | Creates a user with the ADMIN role |
| /users/newUser | POST | OWNER, ADMIN | Creates a user with the USER role |
| /users | GET | OWNER, ADMIN | Display all users |
| /users | PUT | OWNER, ADMIN | Update a user |
| /users | DELETE | OWNER | Delete a user |

---

## 2. Bank Account Service

| Endpoint | Method | Authorization | Description |
|----------|--------|---------------|-------------|
| /bank-accounts/getAllBankAccounts | GET | ADMIN | Display all bank accounts |
| /bank-accounts/email | GET | USER | Display the user's bank account |
| /bank-accounts/delete | DELETE | OWNER | Automatically delete the user's account when the user is deleted |
| /bank-accounts/new | POST | ADMIN | Add a new bank account |
| /bank-accounts/update | PUT | ADMIN | Update a bank account |
| /bank-accounts/update/user | PUT | USER | Update the user's bank account (automatically called by the trade service) |

---

## 3. Currency Exchange Service

| Endpoint | Method | Authorization | Description |
|----------|--------|---------------|-------------|
| /currency-exchange | GET | All | Display fiat currency exchange rates |

---

## 4. Currency Conversion Service

| Endpoint | Method | Authorization | Description |
|----------|--------|---------------|-------------|
| /currency-conversion?from=X&to=Y&quantity=Q | GET | USER | Fiat currency conversion |

---

## 5. Crypto Wallet Service

| Endpoint | Method | Authorization | Description |
|----------|--------|---------------|-------------|
| /crypto-wallets/new | POST | ADMIN | Create a crypto wallet for a user |
| /crypto-wallets/update | PUT | ADMIN | Update a crypto wallet |
| /crypto-wallets/delete | DELETE | OWNER | Delete a crypto wallet |
| /crypto-wallets/all | GET | ADMIN | Display all crypto wallets |
| /crypto-wallets/email | GET | USER | Display the user's crypto wallet |

---

## 6. Crypto Exchange Service

| Endpoint | Method | Authorization | Description |
|----------|--------|---------------|-------------|
| /crypto-exchange | GET | All | Display crypto currency exchange rates |

---

## 7. Crypto Conversion Service

| Endpoint | Method | Authorization | Description |
|----------|--------|---------------|-------------|
| /crypto-conversion?from=X&to=Y&quantity=Q | GET | USER | Crypto currency conversion |

---

## 8. Trade Service

| Endpoint | Method | Authorization | Description |
|----------|--------|---------------|-------------|
| /trade-service?from=X&to=Y&quantity=Q | GET | USER | Fiat to crypto and crypto to fiat currency exchange |

---

## User Credentials

| Email | Password | Role |
|-------|---------|------|
| admin@uns.ac.rs | adminPassword | ADMIN |
| owner@uns.ac.rs | ownerPassword | OWNER |
| user@uns.ac.rs | userPassword | USER |




