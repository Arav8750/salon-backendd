-- =====================================================
--   SMART SALON MANAGEMENT SYSTEM
--   Complete Database Setup Script
--   Run this in MySQL Workbench or mysql terminal
-- =====================================================

-- Step 1: Create and use database
DROP DATABASE IF EXISTS smart_salon_db;
CREATE DATABASE smart_salon_db;
USE smart_salon_db;

-- =====================================================
--   TABLE: users
-- =====================================================
CREATE TABLE users (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100)  NOT NULL,
    email        VARCHAR(150)  NOT NULL UNIQUE,
    password     VARCHAR(255)  NOT NULL,
    phone        VARCHAR(15)   NOT NULL,
    role         ENUM('ADMIN', 'BARBER', 'CUSTOMER') NOT NULL,
    is_active    TINYINT(1)    DEFAULT 1,
    created_at   DATETIME      DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
--   TABLE: services
-- =====================================================
CREATE TABLE services (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(100)   NOT NULL,
    description       VARCHAR(500),
    price             DOUBLE         NOT NULL,
    duration_minutes  INT            NOT NULL,
    is_active         TINYINT(1)     DEFAULT 1
);

-- =====================================================
--   TABLE: barbers
-- =====================================================
CREATE TABLE barbers (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            BIGINT         NOT NULL UNIQUE,
    specialization     VARCHAR(100),
    experience_years   INT            DEFAULT 0,
    status             ENUM('AVAILABLE', 'BUSY', 'OFF_DUTY') DEFAULT 'AVAILABLE',
    is_active          TINYINT(1)     DEFAULT 1,
    CONSTRAINT fk_barber_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =====================================================
--   TABLE: appointments
-- =====================================================
CREATE TABLE appointments (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id            BIGINT        NOT NULL,
    barber_id              BIGINT,
    service_id             BIGINT        NOT NULL,
    appointment_date       DATE          NOT NULL,
    appointment_time       TIME          NOT NULL,
    token_number           INT,
    status                 ENUM('WAITING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'WAITING',
    estimated_wait_minutes INT           DEFAULT 0,
    notes                  VARCHAR(500),
    created_at             DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appt_customer FOREIGN KEY (customer_id) REFERENCES users(id),
    CONSTRAINT fk_appt_barber   FOREIGN KEY (barber_id)   REFERENCES barbers(id),
    CONSTRAINT fk_appt_service  FOREIGN KEY (service_id)  REFERENCES services(id)
);

-- =====================================================
--   TABLE: tokens
-- =====================================================
CREATE TABLE tokens (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    token_number   INT      NOT NULL,
    token_date     DATE     NOT NULL,
    appointment_id BIGINT   NOT NULL UNIQUE,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_token_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

-- =====================================================
--   SEED DATA
-- =====================================================

-- ── Admin User ────────────────────────────────────────
-- Password: Admin@123 (BCrypt hashed)
INSERT INTO users (name, email, password, phone, role, is_active) VALUES
('Admin', 'admin@salon.com',
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTAszSGDVoy',
 '9999999999', 'ADMIN', 1);

-- ── Barber Users ─────────────────────────────────────
-- Password: Barber@123 (BCrypt hashed)
INSERT INTO users (name, email, password, phone, role, is_active) VALUES
('Ravi Kumar',   'ravi@salon.com',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '9111111111', 'BARBER', 1),
('Suresh Patel', 'suresh@salon.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '9222222222', 'BARBER', 1),
('Arjun Singh',  'arjun@salon.com',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '9333333333', 'BARBER', 1);

-- ── Sample Customer Users ─────────────────────────────
-- Password: Customer@123 (BCrypt hashed)
INSERT INTO users (name, email, password, phone, role, is_active) VALUES
('Amit Sharma',  'amit@gmail.com',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '9444444441', 'CUSTOMER', 1),
('Priya Nair',   'priya@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '9444444442', 'CUSTOMER', 1),
('Rahul Verma',  'rahul@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '9444444443', 'CUSTOMER', 1);

-- ── Barber Profiles ───────────────────────────────────
INSERT INTO barbers (user_id, specialization, experience_years, status, is_active) VALUES
((SELECT id FROM users WHERE email = 'ravi@salon.com'),   'Hair & Beard',  5, 'AVAILABLE', 1),
((SELECT id FROM users WHERE email = 'suresh@salon.com'), 'Hair Color',    3, 'AVAILABLE', 1),
((SELECT id FROM users WHERE email = 'arjun@salon.com'),  'All Services',  7, 'AVAILABLE', 1);

-- ── Services ──────────────────────────────────────────
INSERT INTO services (name, description, price, duration_minutes, is_active) VALUES
('Haircut',            'Classic haircut with styling',                 200.00,  30, 1),
('Beard Trim',         'Professional beard shaping and trim',          150.00,  20, 1),
('Hair + Beard Combo', 'Complete haircut and beard grooming package',  300.00,  45, 1),
('Head Massage',       'Relaxing scalp massage with oil',              250.00,  30, 1),
('Hair Color',         'Full hair coloring with premium color',        800.00,  90, 1),
('Facial',             'Deep cleansing facial treatment',              500.00,  60, 1),
('Kids Haircut',       'Gentle haircut for children under 12',         150.00,  20, 1),
('Shave',              'Clean hot-towel straight razor shave',         100.00,  15, 1);

-- ── Sample Appointments (today's date) ───────────────
INSERT INTO appointments
  (customer_id, barber_id, service_id, appointment_date, appointment_time, token_number, status, estimated_wait_minutes, notes)
VALUES
(
  (SELECT id FROM users WHERE email = 'amit@gmail.com'),
  (SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'ravi@salon.com')),
  (SELECT id FROM services WHERE name = 'Haircut'),
  CURDATE(), '10:00:00', 1, 'COMPLETED', 0, 'Regular customer'
),
(
  (SELECT id FROM users WHERE email = 'priya@gmail.com'),
  (SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'ravi@salon.com')),
  (SELECT id FROM services WHERE name = 'Hair Color'),
  CURDATE(), '10:30:00', 2, 'IN_PROGRESS', 30, NULL
),
(
  (SELECT id FROM users WHERE email = 'rahul@gmail.com'),
  (SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'suresh@salon.com')),
  (SELECT id FROM services WHERE name = 'Hair + Beard Combo'),
  CURDATE(), '11:00:00', 3, 'WAITING', 45, 'Wants a fade haircut'
);

-- ── Token records for the above appointments ──────────
INSERT INTO tokens (token_number, token_date, appointment_id) VALUES
(1, CURDATE(), (SELECT id FROM appointments WHERE token_number = 1 AND appointment_date = CURDATE())),
(2, CURDATE(), (SELECT id FROM appointments WHERE token_number = 2 AND appointment_date = CURDATE())),
(3, CURDATE(), (SELECT id FROM appointments WHERE token_number = 3 AND appointment_date = CURDATE()));

-- =====================================================
--   VERIFY: Quick check queries
-- =====================================================
SELECT '=== USERS ===' AS '';
SELECT id, name, email, role, is_active, created_at FROM users;

SELECT '=== SERVICES ===' AS '';
SELECT id, name, price, duration_minutes, is_active FROM services;

SELECT '=== BARBERS ===' AS '';
SELECT b.id, u.name, u.email, b.specialization, b.experience_years, b.status
FROM barbers b JOIN users u ON b.user_id = u.id;

SELECT '=== APPOINTMENTS TODAY ===' AS '';
SELECT
    a.id,
    a.token_number        AS token,
    cu.name               AS customer,
    bu.name               AS barber,
    s.name                AS service,
    a.appointment_date,
    a.appointment_time,
    a.status,
    a.estimated_wait_minutes AS est_wait_min
FROM appointments a
JOIN users    cu ON a.customer_id = cu.id
LEFT JOIN barbers b  ON a.barber_id  = b.id
LEFT JOIN users  bu  ON b.user_id    = bu.id
JOIN services s  ON a.service_id  = s.id
WHERE a.appointment_date = CURDATE()
ORDER BY a.token_number;

-- =====================================================
--   LOGIN CREDENTIALS SUMMARY
-- =====================================================
--   ADMIN    : admin@salon.com    / Admin@123
--   BARBER 1 : ravi@salon.com     / Barber@123
--   BARBER 2 : suresh@salon.com   / Barber@123
--   BARBER 3 : arjun@salon.com    / Barber@123
--   CUSTOMER1: amit@gmail.com     / Customer@123
--   CUSTOMER2: priya@gmail.com    / Customer@123
--   CUSTOMER3: rahul@gmail.com    / Customer@123
--   (Or register new customers from the app)
-- =====================================================
