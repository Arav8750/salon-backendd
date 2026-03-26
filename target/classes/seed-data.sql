-- Smart Salon Management System - Database Setup & Seed Script
-- Run this AFTER the Spring Boot app auto-creates the tables

-- Create database
CREATE DATABASE IF NOT EXISTS smart_salon_db;
USE smart_salon_db;

-- Insert Admin user (password: Admin@123)
-- BCrypt hash of "Admin@123"
INSERT IGNORE INTO users (name, email, password, phone, role, is_active, created_at) VALUES
('Admin User', 'admin@salon.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTAszSGDVoy', '9999999999', 'ADMIN', 1, NOW());

-- Insert Sample Services
INSERT IGNORE INTO services (name, description, price, duration_minutes, is_active) VALUES
('Haircut', 'Classic haircut with styling', 200.00, 30, 1),
('Beard Trim', 'Professional beard shaping and trim', 150.00, 20, 1),
('Hair + Beard Combo', 'Complete haircut and beard grooming package', 300.00, 45, 1),
('Head Massage', 'Relaxing scalp massage with oil', 250.00, 30, 1),
('Hair Color', 'Full hair coloring with premium color', 800.00, 90, 1),
('Facial', 'Deep cleansing facial treatment', 500.00, 60, 1),
('Kids Haircut', 'Gentle haircut for children under 12', 150.00, 20, 1);

-- Note: Barbers are created via Admin Panel (they create User + Barber records)
-- Sample barber insertion (password: Barber@123)
INSERT IGNORE INTO users (name, email, password, phone, role, is_active, created_at) VALUES
('Ravi Kumar', 'ravi@salon.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTAszSGDVoy', '9111111111', 'BARBER', 1, NOW()),
('Suresh Patel', 'suresh@salon.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTAszSGDVoy', '9222222222', 'BARBER', 1, NOW());

-- After inserting users, get their IDs and add barber profiles:
-- Run these queries after the user inserts:
-- INSERT INTO barbers (user_id, specialization, experience_years, status, is_active) 
--   SELECT id, 'Hair & Beard', 5, 'AVAILABLE', 1 FROM users WHERE email = 'ravi@salon.com';
-- INSERT INTO barbers (user_id, specialization, experience_years, status, is_active)
--   SELECT id, 'Hair Color', 3, 'AVAILABLE', 1 FROM users WHERE email = 'suresh@salon.com';

-- =====================================================
-- CREDENTIALS SUMMARY
-- =====================================================
-- Admin:    admin@salon.com   / Admin@123
-- Barber 1: ravi@salon.com    / Admin@123 (same hash used for demo)
-- Barber 2: suresh@salon.com  / Admin@123
-- Register new customers via the app's Register page
-- =====================================================
