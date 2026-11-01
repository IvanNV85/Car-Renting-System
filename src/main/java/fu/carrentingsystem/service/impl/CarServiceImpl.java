package fu.carrentingsystem.service.impl;

import fu.carrentingsystem.constant.CarStatus;
import fu.carrentingsystem.dto.request.CarRequest;
import fu.carrentingsystem.dto.response.CarResponse;
import fu.carrentingsystem.entity.Car;
import fu.carrentingsystem.entity.CarProducer;
import fu.carrentingsystem.repository.CarProducerRepository;
import fu.carrentingsystem.repository.CarRentalRepository;
import fu.carrentingsystem.repository.CarRepository;
import fu.carrentingsystem.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarProducerRepository carProducerRepository;
    private final CarRentalRepository carRentalRepository;

    @Override
    public Page<CarResponse> getAllCars(int page, int size, String keyword, CarStatus status) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), size, Sort.by("carId").descending());
        Page<Car> cars;

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null;

        if (hasKeyword && hasStatus) {
            cars = carRepository.findByCarNameContainingIgnoreCaseAndStatus(keyword.trim(), status, pageable);
        } else if (hasKeyword) {
            cars = carRepository.findByCarNameContainingIgnoreCase(keyword.trim(), pageable);
        } else if (hasStatus) {
            cars = carRepository.findByStatus(status, pageable);
        } else {
            cars = carRepository.findAll(pageable);
        }

        return cars.map(this::mapToResponse);
    }

    @Override
    public CarResponse getCarDetail(int carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + carId));
        return mapToResponse(car);
    }

    @Override
    public CarRequest getCarUpdateForm(int carId) {
        Car c = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + carId));
        return CarRequest.builder()
                .carName(c.getCarName())
                .carModelYear(c.getCarModelYear())
                .color(c.getColor())
                .capacity(c.getCapacity())
                .description(c.getDescription())
                .importDate(c.getImportDate())
                .producerId(c.getCarProducer() != null ? c.getCarProducer().getProducerId() : null)
                .rentPrice(c.getRentPrice())
                .status(c.getStatus())
                .build();
    }

    @Override
    @Transactional
    public void createCar(CarRequest request) {
        CarProducer producer = carProducerRepository.findById(request.getProducerId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà sản xuất xe với ID: " + request.getProducerId()));

        Car car = new Car();
        car.setCarName(request.getCarName().trim());
        car.setCarModelYear(request.getCarModelYear());
        car.setColor(request.getColor().trim());
        car.setCapacity(request.getCapacity());
        car.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        car.setImportDate(request.getImportDate());
        car.setCarProducer(producer);
        car.setRentPrice(request.getRentPrice());
        car.setStatus(request.getStatus());

        carRepository.save(car);
    }

    @Override
    @Transactional
    public void updateCar(int carId, CarRequest request) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + carId));

        CarProducer producer = carProducerRepository.findById(request.getProducerId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà sản xuất xe với ID: " + request.getProducerId()));

        car.setCarName(request.getCarName().trim());
        car.setCarModelYear(request.getCarModelYear());
        car.setColor(request.getColor().trim());
        car.setCapacity(request.getCapacity());
        car.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        car.setImportDate(request.getImportDate());
        car.setCarProducer(producer);
        car.setRentPrice(request.getRentPrice());
        car.setStatus(request.getStatus());

        carRepository.save(car);
    }

    @Override
    @Transactional
    public void deleteCar(int carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + carId));

        if (car.getStatus() == CarStatus.RENTED) {
            throw new IllegalStateException("Không thể xóa xe đang trong trạng thái ĐANG THUÊ (RENTED)!");
        }

        if (carRentalRepository.existsByCar_CarId(carId)) {
            throw new IllegalStateException("Không thể xóa xe vì đã có dữ liệu giao dịch thuê xe trong hệ thống!");
        }

        carRepository.delete(car);
    }

    @Override
    public List<CarProducer> getAllProducers() {
        return carProducerRepository.findAll();
    }

    private CarResponse mapToResponse(Car c) {
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

