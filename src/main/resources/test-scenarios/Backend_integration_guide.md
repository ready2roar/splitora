# 🚀 Splitora Backend Integration Guide

Welcome to the Splitora backend! This document provides everything you need to know to integrate the React frontend with our Spring Boot REST API.

* **Base URL:** `http://localhost:8080/api`
* **Data Format:** JSON (Request & Response)
* **Current Database State:** Pre-populated with dummy users (IDs: `1` for Alice, `2` for Bob, `3` for Charlie) and a default group (`ID: 1`, "Flatmates").

---

## 📋 1. Core API Endpoints

### A. Create an Expense (`POST /api/expenses`)

Handles creating an expense and automatically calculates individual shares using the configured split strategy (**`EQUAL`**, **`EXACT`**, or **`PERCENTAGE`**).

* **URL:** `/api/expenses`
* **Method:** `POST`
* **Content-Type:** `application/json`

#### **Request Payload Examples:**

**1. Equal Split Example:**

```json
{
  "description": "Dinner at Italian place",
  "amount": 30.00,
  "paidByUserId": 1,
  "groupId": 1,
  "splitType": "EQUAL",
  "splits": [
    { "userId": 1, "value": 0 },
    { "userId": 2, "value": 0 },
    { "userId": 3, "value": 0 }
  ]
}

```

**2. Exact Split Example:**

```json
{
  "description": "Supermarket Groceries",
  "amount": 100.00,
  "paidByUserId": 2,
  "groupId": 1,
  "splitType": "EXACT",
  "splits": [
    { "userId": 1, "value": 40.00 },
    { "userId": 2, "value": 35.00 },
    { "userId": 3, "value": 25.00 }
  ]
}

```

**3. Percentage Split Example:**

```json
{
  "description": "Weekend Cabin Rental",
  "amount": 300.00,
  "paidByUserId": 3,
  "groupId": 1,
  "splitType": "PERCENTAGE",
  "splits": [
    { "userId": 1, "value": 50.0 },
    { "userId": 2, "value": 30.0 },
    { "userId": 3, "value": 20.0 }
  ]
}

```

#### **Success Response (`201 Created`):**

```json
{
  "id": 1,
  "description": "Dinner at Italian place",
  "amount": 30.00,
  "paidByUserId": 1,
  "paidByName": "Alice",
  "groupId": 1,
  "splitType": "EQUAL",
  "createdAt": "2026-10-26T12:00:00",
  "splits": [
    { "userId": 1, "userName": "Alice", "amountOwed": 10.00 },
    { "userId": 2, "userName": "Bob", "amountOwed": 10.00 },
    { "userId": 3, "userName": "Charlie", "amountOwed": 10.00 }
  ]
}

```

---

### B. Get User Balance Summary (`GET /api/balances/user/{userId}`)

Aggregates all historical expenses, split shares, and direct settlements to return a user's net financial status and pairwise breakdown with other users.

* **URL:** `/api/balances/user/{userId}`
* **Method:** `GET`
* **Path Variable:** `userId` (e.g., `1`)

#### **Success Response (`200 OK`):**

```json
{
  "userId": 1,
  "userName": "Alice",
  "netBalance": 15.00,
  "breakdown": [
    {
      "userId": 2,
      "userName": "Bob",
      "amount": 15.00
    }
  ]
}

```

* **How to interpret breakdown amounts:**
* **Positive amount (`+15.00`):** That person owes the target user money.
* **Negative amount (`-15.00`):** The target user owes that person money.
* **Net Balance:** The sum total of all balances across all users.



---

### C. Record a Settlement (`POST /api/settlements`)

Records an out-of-band payment (Venmo, UPI, cash) from one user to another, which directly clears out or reduces their balance.

* **URL:** `/api/settlements`
* **Method:** `POST`
* **Content-Type:** `application/json`

#### **Request Payload:**

```json
{
  "payerId": 2,
  "payeeId": 1,
  "amount": 15.00
}

```

#### **Success Response (`201 Created`):**

```json
{
  "id": 1,
  "payerId": 2,
  "payerName": "Bob",
  "payeeId": 1,
  "payeeName": "Alice",
  "amount": 15.00,
  "createdAt": "2026-10-26T12:05:00"
}

```

---

## ⚠️ 2. Error Handling Format

If validation fails (e.g., exact split amounts don't match the total bill, percentages don't equal 100%, or user IDs don't exist), the backend returns a `400 Bad Request` with this standard JSON structure:

```json
{
  "timestamp": "2026-10-26T12:10:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Sum of exact split amounts (90.00) does not match total expense amount (100.00)."
}

```

---

## 🛠️ 3. Frontend Integration Tips

1. **State Management:** When building the "Add Expense" form in React, dynamically render the `splits` array based on whether the user picks `EQUAL`, `EXACT`, or `PERCENTAGE`. For `EQUAL` splits, you can pass `0` for the split values since the backend calculates the equal share automatically.
2. **Polling / Refreshing Balances:** After a successful expense creation (`POST /api/expenses`) or settlement (`POST /api/settlements`), make sure to trigger a re-fetch of the dashboard balance endpoint (`GET /api/balances/user/{id}`) so the UI instantly reflects updated debts.

---