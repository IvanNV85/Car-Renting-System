---
name: car-rental-frontend-design
description: >-
  Use this skill when designing, building, or improving frontend UI/UX templates
  for the Car Renting System using Thymeleaf, Bootstrap 5, Bootstrap Icons, and Spring Security.
---

# Car Rental Frontend Design Skill

Hướng dẫn thiết kế giao diện (UI/UX) chuẩn hóa cho dự án **FU Car Renting System**, đảm bảo tính đồng bộ, hiện đại, thân thiện trên di động (responsive) và tích hợp mượt mà với Spring Boot + Thymeleaf + Spring Security.

---

## 1. Design System & Palette (Quy chuẩn giao diện)

* **CSS Framework**: Bootstrap 5.3 (CDN) + Bootstrap Icons 1.11+.
* **Font chữ**: `'Segoe UI', Roboto, Helvetica, Arial, sans-serif`.
* **Bảng màu chủ đạo**:
  * Nền chung (Background): `#f8fafc` (Slate 50 nhẹ nhàng, dịu mắt).
  * Dark Header & Hero: Gradient `linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f172a 100%)`.
  * Màu thương hiệu (Primary): `#0d6efd` (Bootstrap Blue) hoặc `#2563eb`.
  * Trạng thái xe (Badges):
    * Sẵn sàng (`AVAILABLE`): `<span class="badge bg-success-subtle text-success border border-success-subtle">Sẵn sàng</span>`
    * Đang thuê (`RENTED`): `<span class="badge bg-warning-subtle text-warning border border-warning-subtle">Đang thuê</span>`
    * Bảo dưỡng (`MAINTENANCE`): `<span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle">Bảo dưỡng</span>`
* **Card & Hiệu ứng hover**:
  ```css
  .card-hover {
      border: 1px solid rgba(0, 0, 0, 0.05);
      border-radius: 1rem;
      transition: transform 0.2s ease, box-shadow 0.2s ease;
  }
  .card-hover:hover {
      transform: translateY(-4px);
      box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
  }
  ```

---

## 2. Kiến trúc Thymeleaf Layout (Thymeleaf Layout Architecture)

Để đảm bảo nguyên tắc **DRY (Don't Repeat Yourself)** và đồng bộ 100% giao diện toàn hệ thống, toàn bộ các trang con (Customer, Admin, Public) đều phải sử dụng cơ chế **Thymeleaf Master Layout & Fragments**.

### A. Cấu trúc thư mục templates chuẩn

```text
src/main/resources/templates/
├── layout/
│   ├── main.html           # Master Layout chính (vỏ bọc chung toàn web)
│   ├── navbar.html         # Fragment thanh menu điều hướng
│   ├── footer.html         # Fragment chân trang
│   └── alerts.html         # Fragment thông báo Flash (thành công/lỗi)
├── home_page.html          # Trang chủ (kế thừa layout/main.html)
├── login.html              # Trang đăng nhập
├── register.html           # Trang đăng ký
├── cars/
│   ├── list.html           # Danh sách xe (kế thừa layout/main.html)
│   └── detail.html         # Chi tiết xe & đặt xe
└── admin/
    ├── cars.html           # Quản lý xe (kế thừa layout/main.html)
    ├── rentals.html        # Quản lý đơn thuê xe
    └── customers.html      # Quản lý khách hàng
```

---

### B. Master Layout File (`templates/layout/main.html`)

Sử dụng cơ chế Parameterized Fragments (`th:fragment="layout(...)"`), nhận vào:
* `pageTitle`: Thẻ `<title>` động của trang con.
* `content`: Khối nội dung chính của trang con.
* `customCss`: Thẻ `<style>` hoặc `<link>` riêng của trang (tùy chọn).
* `customScripts`: Thẻ `<script>` JS riêng của trang (tùy chọn).

```html
<!DOCTYPE html>
<html lang="vi" 
      xmlns:th="http://www.thymeleaf.org" 
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
      th:fragment="layout(pageTitle, content, customCss, customScripts)">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title th:replace="${pageTitle}">FU Car Renting System</title>

    <!-- Bootstrap 5.3 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <!-- Global App Styles -->
    <style>
        body {
            background-color: #f8fafc;
            font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
        }
        .card-hover {
            border: 1px solid rgba(0, 0, 0, 0.05);
            border-radius: 1rem;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .card-hover:hover {
            transform: translateY(-4px);
            box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
        }
    </style>

    <!-- Custom CSS từ từng trang con -->
    <th:block th:replace="${customCss} ?: ~{}"></th:block>
</head>
<body class="d-flex flex-column min-vh-100">

    <!-- 1. Navbar chung -->
    <nav th:replace="~{layout/navbar :: navbar}"></nav>

    <!-- 2. Flash Messages (thành công / lỗi) -->
    <div th:replace="~{layout/alerts :: alerts}"></div>

    <!-- 3. Nội dung trang con (Dynamic Main Content) -->
    <main class="flex-grow-1" th:insert="${content}"></main>

    <!-- 4. Footer chung -->
    <footer th:replace="~{layout/footer :: footer}"></footer>

    <!-- Bootstrap 5.3 Bundle JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

    <!-- Custom JS từ từng trang con -->
    <th:block th:replace="${customScripts} ?: ~{}"></th:block>
</body>
</html>
```

---

### C. Mẫu trang con kế thừa Master Layout (`templates/cars/list.html`)

Các trang con **không cần khai báo lại** `<head>`, CDN Bootstrap, Navbar hay Footer. Trang chỉ cần chỉ định các block nội dung:

```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layout/main :: layout(~{::title}, ~{::#main-content}, ~{::#page-css}, ~{::#page-scripts})}">
<head>
    <!-- Tiêu đề trang -->
    <title>Danh Sách Xe Cho Thuê - FU Car Renting</title>

    <!-- CSS riêng cho trang này (nếu không có thì để trống block) -->
    <th:block id="page-css">
        <style>
            .car-filter-box {
                background: #ffffff;
                border-radius: 1rem;
            }
        </style>
    </th:block>
</head>
<body>

    <!-- Nội dung chính sẽ được chèn vào <main> của layout/main.html -->
    <div id="main-content" class="container py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold text-dark mb-1">Đội Xe Cho Thuê</h2>
                <p class="text-muted mb-0">Khám phá và lựa chọn phương tiện phù hợp với hành trình của bạn</p>
            </div>
            <a th:href="@{/cars/compare}" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-arrow-left-right me-1"></i> So sánh xe
            </a>
        </div>

        <!-- Danh sách thẻ xe (Sử dụng Car Card Pattern ở mục 3.B) -->
        <div class="row g-4">
            <!-- Render danh sách xe th:each ... -->
        </div>
    </div>

    <!-- Javascript riêng cho trang này -->
    <th:block id="page-scripts">
        <script>
            console.log("Trang danh sách xe đã nạp xong!");
        </script>
    </th:block>
</body>
</html>
```

---

### D. Các Fragments thành phần trong `templates/layout/`

#### 1. `templates/layout/navbar.html`:
```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<nav th:fragment="navbar" class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center gap-2" th:href="@{/}">
            <i class="bi bi-car-front-fill text-primary fs-4"></i>
            <span class="fw-bold">FU Car Renting</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navContent">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link" th:href="@{/cars}">Danh sách xe</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/about}">Giới thiệu</a></li>
                
                <!-- Menu Admin (chỉ hiển thị khi có quyền ADMIN) -->
                <li class="nav-item dropdown" sec:authorize="hasRole('ADMIN')">
                    <a class="nav-link dropdown-toggle text-warning fw-semibold" href="#" data-bs-toggle="dropdown">
                        <i class="bi bi-shield-lock-fill me-1"></i> Quản trị
                    </a>
                    <ul class="dropdown-menu shadow">
                        <li><a class="dropdown-item" th:href="@{/admin/cars}"><i class="bi bi-car-front me-2"></i>Quản lý xe</a></li>
                        <li><a class="dropdown-item" th:href="@{/admin/rentals}"><i class="bi bi-file-earmark-text me-2"></i>Quản lý đơn thuê</a></li>
                        <li><a class="dropdown-item" th:href="@{/admin/customers}"><i class="bi bi-people me-2"></i>Quản lý khách hàng</a></li>
                    </ul>
                </li>
            </ul>

            <!-- Auth Buttons & Profile Dropdown -->
            <div class="d-flex align-items-center gap-2">
                <div sec:authorize="isAnonymous()">
                    <a th:href="@{/login}" class="btn btn-outline-light btn-sm me-2">Đăng nhập</a>
                    <a th:href="@{/register}" class="btn btn-primary btn-sm">Đăng ký</a>
                </div>
                <div sec:authorize="isAuthenticated()" class="dropdown">
                    <button class="btn btn-dark dropdown-toggle d-flex align-items-center gap-2 border border-secondary" type="button" data-bs-toggle="dropdown">
                        <i class="bi bi-person-circle fs-5 text-primary"></i>
                        <span sec:authentication="name" class="small fw-semibold">User</span>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end shadow">
                        <li><a class="dropdown-item" th:href="@{/profile}"><i class="bi bi-person me-2"></i>Hồ sơ tài khoản</a></li>
                        <li><a class="dropdown-item" th:href="@{/my-rentals}"><i class="bi bi-clock-history me-2"></i>Lịch sử thuê xe</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li>
                            <form th:action="@{/logout}" method="post" class="m-0">
                                <button type="submit" class="dropdown-item text-danger">
                                    <i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
                                </button>
                            </form>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</nav>
</html>
```

#### 2. `templates/layout/footer.html`:
```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org">
<footer th:fragment="footer" class="bg-dark text-white-50 py-4 mt-auto border-top border-secondary">
    <div class="container text-center">
        <div class="d-flex align-items-center justify-content-center gap-2 mb-2">
            <i class="bi bi-car-front-fill text-primary fs-5"></i>
            <span class="text-white fw-bold">FU Car Renting System</span>
        </div>
        <p class="small mb-1">&copy; 2026 FU Car Renting. Bản quyền thuộc về FPT University HSF302.</p>
        <p class="small mb-0 text-secondary">Hệ thống dịch vụ cho thuê xe tự lái và có tài xế uy tín, nhanh chóng.</p>
    </div>
</footer>
</html>
```

#### 3. `templates/layout/alerts.html`:
```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org">
<div th:fragment="alerts" class="container mt-3">
    <!-- Success Alert -->
    <div th:if="${successMessage}" class="alert alert-success alert-dismissible fade show shadow-sm d-flex align-items-center" role="alert">
        <i class="bi bi-check-circle-fill fs-5 me-2 text-success"></i>
        <div th:text="${successMessage}">Thao tác thành công!</div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <!-- Error Alert -->
    <div th:if="${errorMessage}" class="alert alert-danger alert-dismissible fade show shadow-sm d-flex align-items-center" role="alert">
        <i class="bi bi-exclamation-triangle-fill fs-5 me-2 text-danger"></i>
        <div th:text="${errorMessage}">Đã xảy ra lỗi!</div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</div>
</html>
```

---

---

## 3. Thành phần giao diện cốt lõi (Core UI Components)

### A. Car Card Component (Thẻ hiển thị xe)

Dùng cho trang chủ và trang danh sách xe:

```html
<div class="col-md-6 col-lg-4 mb-4">
    <div class="card h-100 card-hover shadow-sm border-0">
        <div class="position-relative">
            <img th:src="${car.imageUrl != null ? car.imageUrl : '/images/car-placeholder.jpg'}" 
                 class="card-img-top" alt="Car image" style="height: 200px; object-fit: cover; border-top-left-radius: 1rem; border-top-right-radius: 1rem;">
            <div class="position-absolute top-0 end-0 m-2">
                <span th:if="${car.status.name() == 'AVAILABLE'}" class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1">Sẵn sàng</span>
                <span th:if="${car.status.name() == 'RENTED'}" class="badge bg-warning-subtle text-warning border border-warning-subtle px-2 py-1">Đang thuê</span>
            </div>
        </div>
        <div class="card-body d-flex flex-column">
            <div class="d-flex justify-content-between align-items-start mb-2">
                <h5 class="card-title fw-bold mb-0" th:text="${car.carName}">Toyota Camry 2024</h5>
                <span class="badge bg-light text-dark border" th:text="${car.carProducer?.producerName}">Toyota</span>
            </div>
            <p class="text-muted small mb-3">
                <i class="bi bi-people-fill me-1"></i> <span th:text="${car.capacity}">5</span> chỗ &bull;
                <i class="bi bi-fuel-pump-fill ms-2 me-1"></i> <span th:text="${car.fuelType}">Xăng</span>
            </p>
            <div class="mt-auto pt-2 border-top d-flex justify-content-between align-items-center">
                <div>
                    <span class="fs-5 fw-bold text-primary" th:text="${#numbers.formatDecimal(car.rentPrice, 0, 'COMMA', 0, 'POINT')} + ' đ'">1.000.000 đ</span>
                    <span class="text-muted small">/ngày</span>
                </div>
                <a th:href="@{'/cars/' + ${car.carId}}" class="btn btn-outline-primary btn-sm rounded-pill px-3">
                    Chi tiết <i class="bi bi-arrow-right"></i>
                </a>
            </div>
        </div>
    </div>
</div>
```

---

### B. Filter & Search Bar (Thanh tìm kiếm & lọc xe)

```html
<div class="card border-0 shadow-sm p-3 mb-4">
    <form th:action="@{/cars}" method="get" class="row g-2 align-items-end">
        <div class="col-md-4">
            <label class="form-label small fw-semibold text-muted">Từ khóa</label>
            <div class="input-group">
                <span class="input-group-text bg-light border-end-0"><i class="bi bi-search text-muted"></i></span>
                <input type="text" name="keyword" th:value="${param.keyword}" class="form-control border-start-0" placeholder="Tên xe, mẫu xe...">
            </div>
        </div>
        <div class="col-md-3">
            <label class="form-label small fw-semibold text-muted">Hãng xe</label>
            <select name="producerId" class="form-select">
                <option value="">-- Tất cả các hãng --</option>
                <option th:each="p : ${producers}" th:value="${p.producerId}" th:text="${p.producerName}" th:selected="${param.producerId != null && param.producerId[0] == #strings.toString(p.producerId)}"></option>
            </select>
        </div>
        <div class="col-md-3">
            <label class="form-label small fw-semibold text-muted">Số chỗ</label>
            <select name="capacity" class="form-select">
                <option value="">-- Tất cả --</option>
                <option value="4" th:selected="${param.capacity != null && param.capacity[0] == '4'}">4 chỗ</option>
                <option value="5" th:selected="${param.capacity != null && param.capacity[0] == '5'}">5 chỗ</option>
                <option value="7" th:selected="${param.capacity != null && param.capacity[0] == '7'}">7 chỗ</option>
            </select>
        </div>
        <div class="col-md-2">
            <button type="submit" class="btn btn-primary w-100">
                <i class="bi bi-funnel-fill me-1"></i> Lọc
            </button>
        </div>
    </form>
</div>
```

---

### C. Form Validation & CSRF Protection

Mọi Form gửi dữ liệu (POST/PUT/DELETE) bắt buộc:
1. Dùng `th:action="@{...}"` để Thymeleaf tự động inject token CSRF `_csrf`.
2. Sử dụng class `is-invalid` và `invalid-feedback` khi có lỗi Bean Validation:

```html
<div class="mb-3">
    <label for="email" class="form-label fw-semibold">Email</label>
    <input type="email" th:field="*{email}" id="email" 
           class="form-control" th:classappend="${#fields.hasErrors('email')} ? 'is-invalid' : ''" 
           placeholder="name@example.com">
    <div class="invalid-feedback" th:if="${#fields.hasErrors('email')}" th:errors="*{email}">
        Email không hợp lệ
    </div>
</div>
```

---

## 4. Bảng quản trị dữ liệu (Admin Data Table)

Đối với các trang Admin quản lý xe hoặc đơn thuê, áp dụng chuẩn table sau:
* Đặt table trong `.card.border-0.shadow-sm`.
* Sử dụng class: `table table-hover align-middle mb-0`.
* Thead sử dụng `table-light text-uppercase small text-muted`.
* Thao tác dùng nút icon nhóm lại: `<div class="btn-group btn-group-sm">...</div>`.

