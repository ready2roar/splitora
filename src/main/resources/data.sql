CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `groups` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE group_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES `groups`(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_group_user (group_id, user_id)
);

CREATE TABLE expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL, -- Total amount of the expense
    paid_by BIGINT NOT NULL,        -- User who paid the bill
    group_id BIGINT NULL,           -- Nullable (can be a non-group expense)
    split_type ENUM('EQUAL', 'EXACT', 'PERCENTAGE') NOT NULL DEFAULT 'EQUAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (paid_by) REFERENCES users(id),
    FOREIGN KEY (group_id) REFERENCES `groups`(id) ON DELETE SET NULL
);

CREATE TABLE expense_splits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount_owed DECIMAL(10, 2) NOT NULL, -- How much this specific user owes for this bill
    FOREIGN KEY (expense_id) REFERENCES expenses(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE settlements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payer_id BIGINT NOT NULL,       -- User sending the money
    payee_id BIGINT NOT NULL,       -- User receiving the money
    amount DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payer_id) REFERENCES users(id),
    FOREIGN KEY (payee_id) REFERENCES users(id)
);

INSERT INTO `share`.`users`
(`name`, `email`, `password_hash`, `phone_number`)
VALUES
('Alice', 'alice@splitora.com', 'hash', '9899898909'),
('Bob', 'bob@splitora.com', 'hash', '9876543210'),
('Charlie', 'charlie@splitora.com', 'hash', '9812345670'),
('David', 'david@splitora.com', 'hash', '9823456781'),
('Emma', 'emma@splitora.com', 'hash', '9834567892'),
('Sophia', 'sophia@splitora.com', 'hash', '9845678903'),
('Oliver', 'oliver@splitora.com', 'hash', '9856789014'),
('James', 'james@splitora.com', 'hash', '9867890125'),
('Priya', 'priya@splitora.com', 'hash', '9878901236'),
('Rahul', 'rahul@splitora.com', 'hash', '9889012347');

INSERT INTO `groups` (id, name, description, created_by, created_at, updated_at) VALUES
(1, 'Flatmates', 'Apartment bills and groceries', 1, NOW(), NOW());

INSERT INTO group_members (id, group_id, user_id, joined_at) VALUES
(1, 1, 1, NOW()),
(2, 1, 2, NOW()),
(3, 1, 3, NOW());