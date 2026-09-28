/* =====================================================
   FUCarRentingSystemDB - SQL Server script
   Gồm: tạo database, bảng, khóa ngoại, ràng buộc, dữ liệu mẫu
   ===================================================== */

USE [master]
GO

IF DB_ID(N'FUCarRentingSystemDB') IS NOT NULL
BEGIN
    ALTER DATABASE [FUCarRentingSystemDB] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE [FUCarRentingSystemDB];
END
GO

CREATE DATABASE [FUCarRentingSystemDB]
GO

USE [FUCarRentingSystemDB]
GO

/* ===================== TABLES ===================== */

CREATE TABLE [dbo].[Account](
    [AccountID]   INT IDENTITY(1,1) NOT NULL,
    [AccountName] NVARCHAR(100) NOT NULL,
    [Email]       VARCHAR(200)  NOT NULL,
    [Password]    VARCHAR(200)  NOT NULL,   -- lưu BCrypt hash
    [Role]        NVARCHAR(10)  NOT NULL,   -- Admin / Customer
    CONSTRAINT [PK_Account] PRIMARY KEY CLUSTERED ([AccountID]),
    CONSTRAINT [UQ_Account_Email] UNIQUE ([Email]),
    CONSTRAINT [CHK_Account_Role] CHECK ([Role] IN (N'Admin', N'Customer'))
)
GO

CREATE TABLE [dbo].[CarProducer](
    [ProducerID]   INT IDENTITY(1,1) NOT NULL,
    [ProducerName] NVARCHAR(100) NOT NULL,
    [Address]      NVARCHAR(200) NOT NULL,
    [Country]      NVARCHAR(100) NOT NULL,
    CONSTRAINT [PK_CarProducer] PRIMARY KEY CLUSTERED ([ProducerID])
)
GO

CREATE TABLE [dbo].[Car](
    [CarID]        INT IDENTITY(1,1) NOT NULL,
    [CarName]      NVARCHAR(200)  NOT NULL,
    [CarModelYear] INT            NOT NULL,
    [Color]        NVARCHAR(50)   NOT NULL,
    [Capacity]     INT            NOT NULL,
    [Description]  NVARCHAR(1000) NOT NULL,
    [ImportDate]   DATE           NOT NULL,
    [ProducerID]   INT            NOT NULL,
    [RentPrice]    DECIMAL(10,2)  NOT NULL,  -- giá thuê / ngày
    [Status]       NVARCHAR(10)   NOT NULL,  -- Available / Rented / Deleted
    CONSTRAINT [PK_Car] PRIMARY KEY CLUSTERED ([CarID]),
    CONSTRAINT [FK_Car_CarProducer] FOREIGN KEY ([ProducerID])
        REFERENCES [dbo].[CarProducer]([ProducerID]),
    CONSTRAINT [CHK_Car_Capacity]  CHECK ([Capacity] > 0),
    CONSTRAINT [CHK_Car_RentPrice] CHECK ([RentPrice] >= 0),
    CONSTRAINT [CHK_Car_Status]    CHECK ([Status] IN (N'Available', N'Rented', N'Deleted'))
)
GO

CREATE TABLE [dbo].[Customer](
    [CustomerID]    INT IDENTITY(1,1) NOT NULL,
    [FullName]      NVARCHAR(200) NOT NULL,
    [Mobile]        VARCHAR(15)   NOT NULL,
    [Birthday]      DATE          NOT NULL,
    [IdentityCard]  VARCHAR(20)   NOT NULL,
    [LicenceNumber] VARCHAR(20)   NOT NULL,
    [LicenceDate]   DATE          NOT NULL,
    [AccountID]     INT           NOT NULL,
    CONSTRAINT [PK_Customer] PRIMARY KEY CLUSTERED ([CustomerID]),
    CONSTRAINT [FK_Customer_Account] FOREIGN KEY ([AccountID])
        REFERENCES [dbo].[Account]([AccountID]),
    CONSTRAINT [UQ_Customer_Account] UNIQUE ([AccountID])   -- quan hệ 1-1 với Account
)
GO

CREATE TABLE [dbo].[CarRental](
    [CarRenID]   INT IDENTITY(1,1) NOT NULL,
    [CustomerID] INT           NOT NULL,
    [CarID]      INT           NOT NULL,
    [PickupDate] DATE          NOT NULL,
    [ReturnDate] DATE          NOT NULL,
    [RentPrice]  DECIMAL(10,2) NOT NULL,   -- tổng tiền của giao dịch
    [Status]     NVARCHAR(10)  NOT NULL,   -- Active / Completed / Cancelled
    CONSTRAINT [PK_CarRental] PRIMARY KEY CLUSTERED ([CarRenID]),
    CONSTRAINT [FK_CarRental_Customer] FOREIGN KEY ([CustomerID])
        REFERENCES [dbo].[Customer]([CustomerID]),
    CONSTRAINT [FK_CarRental_Car] FOREIGN KEY ([CarID])
        REFERENCES [dbo].[Car]([CarID]),
    CONSTRAINT [CHK_ReturnDate] CHECK ([ReturnDate] > [PickupDate]),
    CONSTRAINT [CHK_CarRental_Status] CHECK ([Status] IN (N'Active', N'Completed', N'Cancelled'))
)
GO

CREATE TABLE [dbo].[Review](
    [ID]         INT IDENTITY(1,1) NOT NULL,
    [CarRenID]   INT           NOT NULL,
    [ReviewStar] INT           NOT NULL,
    [Comment]    NVARCHAR(500) NOT NULL,
    CONSTRAINT [PK_Review] PRIMARY KEY CLUSTERED ([ID]),
    CONSTRAINT [FK_Review_CarRental] FOREIGN KEY ([CarRenID])
        REFERENCES [dbo].[CarRental]([CarRenID]),
    CONSTRAINT [CHK_ReviewStar] CHECK ([ReviewStar] >= 1 AND [ReviewStar] <= 5)
)
GO

/* ===================== SAMPLE DATA ===================== */
/* Mật khẩu mẫu (đã mã hóa BCrypt):
   admin@fu.com     -> admin123
   an@gmail.com     -> 123456
   binh@gmail.com   -> 123456
*/

INSERT INTO [dbo].[Account] ([AccountName], [Email], [Password], [Role]) VALUES
(N'admin',       'admin@fu.com',   '$2a$10$UhgNEUf9QP6xWot/Fza3luNwHDsy/lobiLMeFocpXtQgQxEIiNvd.', N'Admin'),
(N'nguyenvanan', 'an@gmail.com',   '$2a$10$zE.2tH1UfVAIgVNTbJkwFuPRML5TatVBZ1tQcAqLNb.xG50zStOdy', N'Customer'),
(N'tranthibinh', 'binh@gmail.com', '$2a$10$zE.2tH1UfVAIgVNTbJkwFuPRML5TatVBZ1tQcAqLNb.xG50zStOdy', N'Customer')
GO

INSERT INTO [dbo].[CarProducer] ([ProducerName], [Address], [Country]) VALUES
(N'Toyota',  N'1 Toyota-cho, Toyota City, Aichi',        N'Japan'),
(N'Honda',   N'2-1-1 Minami-Aoyama, Minato, Tokyo',      N'Japan'),
(N'VinFast', N'Dinh Vu - Cat Hai Economic Zone, Hai Phong', N'Vietnam'),
(N'Ford',    N'One American Road, Dearborn, Michigan',   N'USA')
GO

INSERT INTO [dbo].[Car] ([CarName],[CarModelYear],[Color],[Capacity],[Description],[ImportDate],[ProducerID],[RentPrice],[Status]) VALUES
(N'Toyota Vios',      2022, N'White',  5, N'Sedan 5 chỗ tiết kiệm nhiên liệu, phù hợp đi trong thành phố.',       '2023-01-15', 1,  700000, N'Available'),
(N'Toyota Innova',    2021, N'Silver', 7, N'MPV 7 chỗ rộng rãi, phù hợp gia đình và đi xa.',                      '2023-02-20', 1,  950000, N'Available'),
(N'Honda CR-V',       2023, N'Black',  7, N'SUV 7 chỗ, động cơ mạnh, nhiều tính năng an toàn.',                   '2023-05-10', 2, 1200000, N'Available'),
(N'Honda City',       2022, N'Red',    5, N'Sedan hạng B, nội thất hiện đại.',                                    '2023-03-05', 2,  750000, N'Available'),
(N'VinFast VF e34',   2023, N'Blue',   5, N'SUV điện 5 chỗ, vận hành êm ái, chi phí thấp.',                       '2023-08-01', 3,  900000, N'Available'),
(N'VinFast Lux A2.0', 2020, N'White',  5, N'Sedan cao cấp, động cơ 2.0 turbo.',                                   '2022-11-11', 3, 1100000, N'Rented'),
(N'Ford Ranger',      2022, N'Orange', 5, N'Bán tải mạnh mẽ, phù hợp địa hình khó.',                              '2023-04-18', 4, 1000000, N'Available'),
(N'Ford Everest',     2023, N'Grey',   7, N'SUV 7 chỗ, khả năng off-road tốt.',                                   '2023-09-09', 4, 1300000, N'Available')
GO

INSERT INTO [dbo].[Customer] ([FullName],[Mobile],[Birthday],[IdentityCard],[LicenceNumber],[LicenceDate],[AccountID]) VALUES
(N'Nguyễn Văn An', '0901234567', '1998-05-12', '001098012345', 'B2-123456', '2019-06-01', 2),
(N'Trần Thị Bình', '0912345678', '2000-09-30', '001200098765', 'B2-654321', '2021-03-15', 3)
GO

INSERT INTO [dbo].[CarRental] ([CustomerID],[CarID],[PickupDate],[ReturnDate],[RentPrice],[Status]) VALUES
(1, 1, '2025-01-10', '2025-01-13', 2100000, N'Completed'),   -- 3 ngày x 700,000
(1, 3, '2025-02-05', '2025-02-07', 2400000, N'Completed'),   -- 2 ngày x 1,200,000
(2, 5, '2025-03-01', '2025-03-05', 3600000, N'Completed'),   -- 4 ngày x 900,000
(2, 6, '2026-09-25', '2026-09-30', 5500000, N'Active')       -- 5 ngày x 1,100,000
GO

INSERT INTO [dbo].[Review] ([CarRenID],[ReviewStar],[Comment]) VALUES
(1, 5, N'Xe sạch sẽ, chạy êm, giao nhận đúng giờ.'),
(2, 4, N'Xe tốt nhưng hơi tốn xăng.'),
(3, 5, N'Xe điện chạy rất êm, sẽ thuê lại.')
GO

/* ===================== QUERY THỐNG KÊ MẪU ===================== */
/* Báo cáo giao dịch thuê từ StartDate đến EndDate, sắp xếp giảm dần */
-- SELECT cr.CarRenID, c.FullName, ca.CarName, cr.PickupDate, cr.ReturnDate, cr.RentPrice, cr.Status
-- FROM CarRental cr
-- JOIN Customer c ON cr.CustomerID = c.CustomerID
-- JOIN Car ca     ON cr.CarID = ca.CarID
-- WHERE cr.PickupDate >= '2025-01-01' AND cr.ReturnDate <= '2025-12-31'
-- ORDER BY cr.RentPrice DESC;
GO
