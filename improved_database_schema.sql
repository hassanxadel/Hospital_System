-- Hospital Management System - Improved Database Schema
-- This schema addresses security, naming conventions, data integrity, and scalability issues

SET FOREIGN_KEY_CHECKS=0;
DROP DATABASE IF EXISTS hospital_management_improved;
CREATE DATABASE hospital_management_improved;
USE hospital_management_improved;

-- =============================================
-- 1. USER MANAGEMENT TABLES
-- =============================================

-- Users table with proper security
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    role ENUM('admin', 'doctor', 'nurse', 'receptionist', 'billing') NOT NULL DEFAULT 'receptionist',
    is_active BOOLEAN DEFAULT TRUE,
    email VARCHAR(100) UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    failed_login_attempts INT DEFAULT 0,
    locked_until TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- User sessions for security tracking
CREATE TABLE user_sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    user_id INT NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_sessions (user_id, is_active),
    INDEX idx_expires (expires_at)
);

-- =============================================
-- 2. PATIENT MANAGEMENT TABLES
-- =============================================

-- Patients table with improved structure
CREATE TABLE patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_number VARCHAR(20) UNIQUE NOT NULL, -- Human-readable patient number
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('M', 'F', 'Other') NOT NULL,
    blood_group ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-') NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(15),
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    postal_code VARCHAR(10),
    country VARCHAR(50) DEFAULT 'India',
    medical_history TEXT,
    allergies TEXT,
    insurance_number VARCHAR(50),
    insurance_provider VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    INDEX idx_patient_number (patient_number),
    INDEX idx_name (last_name, first_name),
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_created_at (created_at)
);

-- =============================================
-- 3. DOCTOR MANAGEMENT TABLES
-- =============================================

-- Doctors table with enhanced information
CREATE TABLE doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_number VARCHAR(20) UNIQUE NOT NULL, -- Human-readable doctor number
    user_id INT UNIQUE, -- Link to user account if doctor has system access
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    qualifications VARCHAR(255) NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    date_of_birth DATE,
    gender ENUM('M', 'F', 'Other'),
    blood_group ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'),
    address TEXT,
    date_of_joining DATE NOT NULL,
    consultation_fee DECIMAL(10,2) DEFAULT 0.00,
    is_available BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    INDEX idx_doctor_number (doctor_number),
    INDEX idx_name (last_name, first_name),
    INDEX idx_specialization (specialization),
    INDEX idx_license (license_number),
    INDEX idx_availability (is_available, is_active)
);

-- Doctor availability schedule
CREATE TABLE doctor_schedules (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT NOT NULL,
    day_of_week ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    UNIQUE KEY unique_doctor_schedule (doctor_id, day_of_week, start_time),
    INDEX idx_doctor_day (doctor_id, day_of_week)
);

-- =============================================
-- 4. ROOM MANAGEMENT TABLES
-- =============================================

-- Room types for better categorization
CREATE TABLE room_types (
    room_type_id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    base_rate DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rooms table with enhanced tracking
CREATE TABLE rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type_id INT NOT NULL,
    floor_number INT,
    capacity INT DEFAULT 1,
    current_rate DECIMAL(10,2) NOT NULL,
    status ENUM('Available', 'Occupied', 'Maintenance', 'Cleaning') DEFAULT 'Available',
    amenities TEXT, -- JSON or comma-separated list of amenities
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_type_id) REFERENCES room_types(room_type_id),
    INDEX idx_room_number (room_number),
    INDEX idx_room_type (room_type_id),
    INDEX idx_status (status),
    INDEX idx_floor (floor_number)
);

-- =============================================
-- 5. ADMISSION AND DISCHARGE TABLES
-- =============================================

-- Patient admissions with comprehensive tracking
CREATE TABLE patient_admissions (
    admission_id INT PRIMARY KEY AUTO_INCREMENT,
    admission_number VARCHAR(20) UNIQUE NOT NULL,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    room_id INT NOT NULL,
    admission_date DATETIME NOT NULL,
    discharge_date DATETIME NULL,
    primary_diagnosis VARCHAR(255) NOT NULL,
    secondary_diagnosis TEXT,
    admission_type ENUM('Emergency', 'Planned', 'Transfer') NOT NULL,
    admission_status ENUM('Active', 'Discharged', 'Transferred', 'Deceased') DEFAULT 'Active',
    estimated_discharge_date DATE,
    special_instructions TEXT,
    admission_notes TEXT,
    discharge_notes TEXT,
    discharge_summary TEXT,
    admitted_by INT NOT NULL,
    discharged_by INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (admitted_by) REFERENCES users(user_id),
    FOREIGN KEY (discharged_by) REFERENCES users(user_id),
    INDEX idx_admission_number (admission_number),
    INDEX idx_patient (patient_id),
    INDEX idx_doctor (doctor_id),
    INDEX idx_room (room_id),
    INDEX idx_admission_date (admission_date),
    INDEX idx_status (admission_status)
);

-- =============================================
-- 6. SERVICES AND BILLING TABLES
-- =============================================

-- Service categories for better organization
CREATE TABLE service_categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Medical services with detailed information
CREATE TABLE medical_services (
    service_id INT PRIMARY KEY AUTO_INCREMENT,
    service_code VARCHAR(20) UNIQUE NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    category_id INT NOT NULL,
    description TEXT,
    standard_rate DECIMAL(10,2) NOT NULL,
    duration_minutes INT DEFAULT 30,
    requires_doctor_approval BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES service_categories(category_id),
    INDEX idx_service_code (service_code),
    INDEX idx_service_name (service_name),
    INDEX idx_category (category_id)
);

-- Patient services (services provided to patients)
CREATE TABLE patient_services (
    patient_service_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    admission_id INT NULL, -- NULL for outpatient services
    service_id INT NOT NULL,
    doctor_id INT NOT NULL,
    service_date DATETIME NOT NULL,
    quantity INT DEFAULT 1,
    unit_rate DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    discount_percentage DECIMAL(5,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    final_amount DECIMAL(10,2) NOT NULL,
    notes TEXT,
    status ENUM('Pending', 'Completed', 'Cancelled') DEFAULT 'Pending',
    performed_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (admission_id) REFERENCES patient_admissions(admission_id),
    FOREIGN KEY (service_id) REFERENCES medical_services(service_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
    FOREIGN KEY (performed_by) REFERENCES users(user_id),
    INDEX idx_patient (patient_id),
    INDEX idx_admission (admission_id),
    INDEX idx_service (service_id),
    INDEX idx_doctor (doctor_id),
    INDEX idx_service_date (service_date),
    INDEX idx_status (status)
);

-- Bills with comprehensive tracking
CREATE TABLE bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    bill_number VARCHAR(20) UNIQUE NOT NULL,
    patient_id INT NOT NULL,
    admission_id INT NULL, -- NULL for outpatient bills
    bill_date DATE NOT NULL,
    due_date DATE NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    tax_percentage DECIMAL(5,2) DEFAULT 0.00,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    discount_percentage DECIMAL(5,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(12,2) NOT NULL,
    paid_amount DECIMAL(12,2) DEFAULT 0.00,
    balance_amount DECIMAL(12,2) NOT NULL,
    status ENUM('Draft', 'Pending', 'Partially_Paid', 'Paid', 'Overdue', 'Cancelled') DEFAULT 'Pending',
    payment_terms VARCHAR(100),
    notes TEXT,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (admission_id) REFERENCES patient_admissions(admission_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    INDEX idx_bill_number (bill_number),
    INDEX idx_patient (patient_id),
    INDEX idx_admission (admission_id),
    INDEX idx_bill_date (bill_date),
    INDEX idx_status (status),
    INDEX idx_balance (balance_amount)
);

-- Bill line items for detailed billing
CREATE TABLE bill_line_items (
    line_item_id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    patient_service_id INT NULL, -- Link to patient_services
    item_type ENUM('Service', 'Room', 'Medicine', 'Equipment', 'Other') NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,3) NOT NULL,
    unit_rate DECIMAL(10,2) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bill_id) REFERENCES bills(bill_id) ON DELETE CASCADE,
    FOREIGN KEY (patient_service_id) REFERENCES patient_services(patient_service_id),
    INDEX idx_bill (bill_id),
    INDEX idx_patient_service (patient_service_id)
);

-- Payment tracking
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    payment_number VARCHAR(20) UNIQUE NOT NULL,
    bill_id INT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('Cash', 'Card', 'Check', 'Bank_Transfer', 'Insurance', 'Other') NOT NULL,
    payment_reference VARCHAR(100), -- Check number, transaction ID, etc.
    notes TEXT,
    processed_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bill_id) REFERENCES bills(bill_id),
    FOREIGN KEY (processed_by) REFERENCES users(user_id),
    INDEX idx_payment_number (payment_number),
    INDEX idx_bill (bill_id),
    INDEX idx_payment_date (payment_date),
    INDEX idx_payment_method (payment_method)
);

-- =============================================
-- 7. AUDIT AND LOGGING TABLES
-- =============================================

-- Audit log for all critical operations
CREATE TABLE audit_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(100) NOT NULL,
    record_id VARCHAR(50) NOT NULL,
    operation ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    old_values JSON,
    new_values JSON,
    user_id INT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_table_record (table_name, record_id),
    INDEX idx_user (user_id),
    INDEX idx_timestamp (timestamp)
);

-- System configuration table
CREATE TABLE system_config (
    config_id INT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    description TEXT,
    is_encrypted BOOLEAN DEFAULT FALSE,
    updated_by INT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (updated_by) REFERENCES users(user_id),
    INDEX idx_config_key (config_key)
);

-- =============================================
-- 8. INSERT SAMPLE DATA
-- =============================================

-- Insert room types
INSERT INTO room_types (type_name, description, base_rate) VALUES
('General', 'Standard room with basic amenities', 2000.00),
('Deluxe', 'Comfortable room with additional amenities', 3500.00),
('ICU', 'Intensive Care Unit with advanced monitoring', 8000.00),
('Private', 'Private room with premium amenities', 5000.00),
('Emergency', 'Emergency room for critical care', 10000.00);

-- Insert service categories
INSERT INTO service_categories (category_name, description) VALUES
('Consultation', 'Doctor consultation services'),
('Diagnostic', 'Laboratory and imaging services'),
('Surgery', 'Surgical procedures'),
('Therapy', 'Physical and occupational therapy'),
('Emergency', 'Emergency medical services');

-- Insert sample medical services
INSERT INTO medical_services (service_code, service_name, category_id, standard_rate, duration_minutes) VALUES
('CONS001', 'General Consultation', 1, 500.00, 30),
('CONS002', 'Specialist Consultation', 1, 800.00, 45),
('DIAG001', 'Blood Test - Complete', 2, 300.00, 15),
('DIAG002', 'X-Ray Chest', 2, 400.00, 20),
('DIAG003', 'CT Scan', 2, 2500.00, 30),
('SURG001', 'Minor Surgery', 3, 15000.00, 120),
('SURG002', 'Major Surgery', 3, 50000.00, 240),
('THER001', 'Physiotherapy Session', 4, 600.00, 60),
('EMER001', 'Emergency Consultation', 5, 1000.00, 30);

-- Insert default admin user (password should be hashed in real implementation)
INSERT INTO users (username, password_hash, salt, role, email, first_name, last_name, phone) VALUES
('admin', '$2a$10$example_hash_here', 'random_salt_here', 'admin', 'admin@hospital.com', 'System', 'Administrator', '1234567890');

-- Insert sample configuration
INSERT INTO system_config (config_key, config_value, description) VALUES
('hospital_name', 'Advanced Hospital Management System', 'Name of the hospital'),
('hospital_address', '123 Healthcare Street, Medical City, MC 12345', 'Hospital address'),
('tax_rate', '18.00', 'Default tax rate percentage'),
('currency', 'INR', 'Currency code'),
('backup_frequency', 'daily', 'Database backup frequency');

-- =============================================
-- 9. CREATE TRIGGERS FOR AUDIT LOGGING
-- =============================================

-- Note: Triggers would be implemented here for automatic audit logging
-- This is a simplified version - full implementation would include triggers
-- for all tables that require audit logging

SET FOREIGN_KEY_CHECKS=1;

-- Create views for common queries
CREATE VIEW patient_details AS
SELECT 
    p.patient_id,
    p.patient_number,
    CONCAT(p.first_name, ' ', p.last_name) AS full_name,
    p.date_of_birth,
    TIMESTAMPDIFF(YEAR, p.date_of_birth, CURDATE()) AS age,
    p.gender,
    p.blood_group,
    p.phone,
    p.email,
    p.address,
    p.is_active
FROM patients p;

CREATE VIEW doctor_details AS
SELECT 
    d.doctor_id,
    d.doctor_number,
    CONCAT(d.first_name, ' ', d.last_name) AS full_name,
    d.specialization,
    d.qualifications,
    d.license_number,
    d.phone,
    d.email,
    d.consultation_fee,
    d.is_available,
    d.is_active
FROM doctors d;

CREATE VIEW room_availability AS
SELECT 
    r.room_id,
    r.room_number,
    rt.type_name AS room_type,
    r.floor_number,
    r.capacity,
    r.current_rate,
    r.status,
    r.amenities
FROM rooms r
JOIN room_types rt ON r.room_type_id = rt.room_type_id
WHERE r.is_active = TRUE;

-- Create indexes for better performance
CREATE INDEX idx_patients_search ON patients(last_name, first_name, patient_number);
CREATE INDEX idx_doctors_search ON doctors(last_name, first_name, doctor_number, specialization);
CREATE INDEX idx_admissions_active ON patient_admissions(admission_status, admission_date);
CREATE INDEX idx_bills_pending ON bills(status, due_date);
CREATE INDEX idx_services_date ON patient_services(service_date, status);

COMMIT; 