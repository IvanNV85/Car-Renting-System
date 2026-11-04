-- ============================================================
-- FU Car Renting System - Test Data Seed Script
-- Database: SQL Server (FUCarRentingSystemDB)
-- Tương đương với: DataInitializer.java
-- ============================================================

-- ============================================================
-- 1. TÀI KHOẢN ADMIN MẶC ĐỊNH
--    Email:    admin@fu.edu.vn
--    Password: admin123 (BCrypt encoded)
-- ============================================================
IF NOT EXISTS (SELECT 1 FROM account WHERE email = N'admin@fu.edu.vn')
BEGIN
    INSERT INTO account (account_name, email, password, role)
    VALUES (
        N'admin',
        N'admin@fu.edu.vn',
        N'$2a$10$/.trRbEtDdUNqr1n3ydHdOLwVarqcp6IqXpQAQKAcg0YV.YlDvfMC',
        N'ADMIN'
    );
    PRINT N'>>> Initialized default Admin account: admin@fu.edu.vn / admin123';
END
GO

-- ============================================================
-- 2. HÃNG XE SẢN XUẤT (Car Producers)
-- ============================================================
IF (SELECT COUNT(*) FROM car_producer) = 0
BEGIN
    INSERT INTO car_producer (producer_name, address, country) VALUES
        (N'Toyota',        N'Toyota City, Aichi',                                    N'Nhật Bản'),
        (N'Honda',         N'Minato, Tokyo',                                         N'Nhật Bản'),
        (N'Hyundai',       N'Seoul',                                                 N'Hàn Quốc'),
        (N'Kia',           N'Seoul',                                                 N'Hàn Quốc'),
        (N'Mazda',         N'Fuchu, Hiroshima',                                      N'Nhật Bản'),
        (N'Ford',          N'Dearborn, Michigan',                                    N'Mỹ'),
        (N'VinFast',       N'Khu kinh tế Đình Vũ - Cát Hải, Hải Phòng',             N'Việt Nam'),
        (N'Mitsubishi',    N'Minato, Tokyo',                                         N'Nhật Bản'),
        (N'Mercedes-Benz', N'Stuttgart',                                             N'Đức'),
        (N'BMW',           N'Munich',                                                N'Đức');

    PRINT N'>>> Initialized 10 default car producers for dropdown selection.';
END
GO

