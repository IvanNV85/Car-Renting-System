package fu.carrentingsystem.service.impl;

import fu.carrentingsystem.constant.CarRentalStatus;
import fu.carrentingsystem.constant.CarStatus;
import fu.carrentingsystem.dto.request.RentalBookingRequest;
import fu.carrentingsystem.dto.response.CarRentalResponse;
import fu.carrentingsystem.dto.response.CarResponse;
import fu.carrentingsystem.entity.Car;
import fu.carrentingsystem.entity.CarRental;
import fu.carrentingsystem.entity.Customer;
import fu.carrentingsystem.repository.CarRentalRepository;
import fu.carrentingsystem.repository.CarRepository;
import fu.carrentingsystem.repository.CustomerRepository;
import fu.carrentingsystem.service.CarRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarRentalServiceImpl implements CarRentalService {

    private final CarRentalRepository carRentalRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void createRentalTransaction(String usernameOrEmail, RentalBookingRequest request) {
        Customer customer = getCustomerByPrincipal(usernameOrEmail);

        if (request.getPickupDate() == null || request.getReturnDate() == null) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ ngày nhận và ngày trả xe.");
        }
        if (request.getPickupDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày nhận xe không thể ở trong quá khứ.");
        }
        if (!request.getReturnDate().isAfter(request.getPickupDate())) {
            throw new IllegalArgumentException("Ngày trả xe phải sau ngày nhận xe ít nhất 1 ngày.");
        }
        if (request.getCarIds() == null || request.getCarIds().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một xe để đặt thuê.");
        }

        long days = ChronoUnit.DAYS.between(request.getPickupDate(), request.getReturnDate());
        if (days <= 0) {
            days = 1;
        }

        for (Integer carId : request.getCarIds()) {
            Car car = carRepository.findById(carId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + carId));

            if (car.getStatus() != CarStatus.AVAILABLE) {
                throw new IllegalStateException("Xe '" + car.getCarName() + "' hiện không ở trạng thái sẵn sàng để thuê (trạng thái: " + car.getStatus() + ").");
            }

            BigDecimal totalRentPrice = BigDecimal.valueOf((long) car.getRentPrice() * days);

            CarRental rental = new CarRental();
            rental.setCustomer(customer);
            rental.setCar(car);
            rental.setPickupDate(request.getPickupDate());
            rental.setReturnDate(request.getReturnDate());
            rental.setRentPrice(totalRentPrice);
            rental.setStatus(CarRentalStatus.ACTIVE);

            carRentalRepository.save(rental);

            car.setStatus(CarStatus.RENTED);
            carRepository.save(car);
        }
    }

    @Override
    public List<CarRentalResponse> getCustomerRentalHistory(String usernameOrEmail) {
        Customer customer = getCustomerByPrincipal(usernameOrEmail);
        return carRentalRepository.findByCustomer_CustomerIdOrderByCarRentIdDesc(customer.getCustomerId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CarRentalResponse getRentalDetail(int carRentId, String usernameOrEmail) {
        Customer customer = getCustomerByPrincipal(usernameOrEmail);
        CarRental rental = carRentalRepository.findByCarRentIdAndCustomer_CustomerId(carRentId, customer.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hợp đồng thuê xe #" + carRentId + " của bạn."));
        return mapToResponse(rental);
    }

    @Override
    @Transactional
    public void cancelRental(int carRentId, String usernameOrEmail) {
        Customer customer = getCustomerByPrincipal(usernameOrEmail);
        CarRental rental = carRentalRepository.findByCarRentIdAndCustomer_CustomerId(carRentId, customer.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hợp đồng thuê xe #" + carRentId + " của bạn."));

        if (rental.getStatus() != CarRentalStatus.ACTIVE) {
            throw new IllegalStateException("Chỉ có thể hủy hợp đồng đang trong trạng thái ACTIVE (Đang thuê).");
        }

        if (!rental.getPickupDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Không thể hủy hợp đồng khi đã đến hoặc qua ngày nhận xe.");
        }

        rental.setStatus(CarRentalStatus.CANCELED);
        carRentalRepository.save(rental);

        Car car = rental.getCar();
        if (car != null) {
            car.setStatus(CarStatus.AVAILABLE);
            carRepository.save(car);
        }
    }

    @Override
    public List<CarResponse> getAvailableCarsForBooking() {
        return carRepository.findByStatus(CarStatus.AVAILABLE).stream()
                .map(this::mapCarToResponse)
                .collect(Collectors.toList());
    }

    private Customer getCustomerByPrincipal(String principal) {
        return customerRepository.findByAccount_Email(principal)
                .or(() -> customerRepository.findByAccount_AccountName(principal))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin khách hàng ứng với tài khoản: " + principal));
    }

    private CarRentalResponse mapToResponse(CarRental r) {
        long days = ChronoUnit.DAYS.between(r.getPickupDate(), r.getReturnDate());
        boolean canCancel = r.getStatus() == CarRentalStatus.ACTIVE && r.getPickupDate().isAfter(LocalDate.now());

        return CarRentalResponse.builder()
                .carRentId(r.getCarRentId())
                .customerId(r.getCustomer() != null ? r.getCustomer().getCustomerId() : 0)
                .customerName(r.getCustomer() != null ? r.getCustomer().getFullName() : "N/A")
                .customerMobile(r.getCustomer() != null ? r.getCustomer().getMobile() : "N/A")
                .carId(r.getCar() != null ? r.getCar().getCarId() : 0)
                .carName(r.getCar() != null ? r.getCar().getCarName() : "N/A")
                .producerName(r.getCar() != null && r.getCar().getCarProducer() != null ? r.getCar().getCarProducer().getProducerName() : "N/A")
                .carModelYear(r.getCar() != null ? r.getCar().getCarModelYear() : 0)
                .color(r.getCar() != null ? r.getCar().getColor() : "N/A")
                .capacity(r.getCar() != null ? r.getCar().getCapacity() : 0)
                .pickupDate(r.getPickupDate())
                .returnDate(r.getReturnDate())
                .totalDays(Math.max(days, 1))
                .rentPrice(r.getRentPrice())
                .status(r.getStatus())
                .canCancel(canCancel)
                .build();
    }

    private CarResponse mapCarToResponse(Car c) {
        return CarResponse.builder()
                .carId(c.getCarId())
                .carName(c.getCarName())
                .carModelYear(c.getCarModelYear())
                .color(c.getColor())
                .capacity(c.getCapacity())
                .description(c.getDescription())
                .importDate(c.getImportDate())
                .producerId(c.getCarProducer() != null ? c.getCarProducer().getProducerId() : 0)
                .producerName(c.getCarProducer() != null ? c.getCarProducer().getProducerName() : "N/A")
                .rentPrice(c.getRentPrice())
                .status(c.getStatus())
                .build();
    }
}

