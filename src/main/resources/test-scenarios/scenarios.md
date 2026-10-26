To run these examples, assume you have 3 users already inserted into your database:

ID 1: Alice (alice@splitora.com)

ID 2: Bob (bob@splitora.com)

ID 3: Charlie (charlie@splitora.com)

And optionally a Group with id: 1 (or you can set groupId: null for 1-on-1 expenses).

Test Scenario 1: Equal Split (Testing Rounding & Remainder Logic)
    The Case: Alice pays $10.00 for coffee and splits it equally among Alice (1), Bob (2), and Charlie (3).

    Expected Result: $10.00 ÷ 3 = $3.333... Since currency needs 2 decimal places, Alice gets $3.34 (absorbing the extra penny), and Bob and Charlie get $3.33 each. 

Request Payload (POST /api/expenses)
{
"description": "Coffee and Pastries",
"amount": 10.00,
"paidByUserId": 1,
"groupId": 1,
"splitType": "EQUAL",
"splits": [
{ "userId": 1, "value": 0 },
{ "userId": 2, "value": 0 },
{ "userId": 3, "value": 0 }
]
}

Expected Response (201 Created)

{
"id": 1,
"description": "Coffee and Pastries",
"amount": 10.00,
"paidByUserId": 1,
"paidByName": "Alice",
"groupId": 1,
"splitType": "EQUAL",
"createdAt": "2026-10-25T...",
"splits": [
{ "userId": 1, "userName": "Alice", "amountOwed": 3.34 },
{ "userId": 2, "userName": "Bob", "amountOwed": 3.33 },
{ "userId": 3, "userName": "Charlie", "amountOwed": 3.33 }
]
}

===============

Test Scenario 2: Exact Split (Testing Validation & Custom Shares)
The Case: Bob pays $100.00 for supermarket groceries. Alice consumed $40.00 worth of items, Bob consumed $35.00, and Charlie consumed $25.00.

Expected Result: The sum of exact values ($40 + $35 + $25 = $100.00) matches the total bill precisely.

Request Payload (POST /api/expenses)

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

(Note: If you try to pass exact values that add up to $99.00 or $101.00, your ExactSplitStrategy will throw a BadRequestException with a 400 Bad Request status, which your global handler will catch).

====================

Test Scenario 3: Percentage Split (Testing Percentage Conversion & Rounding)
The Case: Charlie pays $300.00 for a weekend cabin rental. Alice takes a 50% share, Bob takes 30%, and Charlie takes 20%.

Expected Result: Percentages equal 100%. Converted amounts: Alice owes $150.00, Bob owes $90.00, and Charlie owes $60.00.

Request Payload (POST /api/expenses)

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