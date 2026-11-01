package fu.carrentingsystem.service;

import fu.carrentingsystem.constant.CarStatus;
import fu.carrentingsystem.dto.request.CarRequest;
import fu.carrentingsystem.dto.response.CarResponse;
import fu.carrentingsystem.entity.CarProducer;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CarService {
    Page<CarResponse> getAllCars(int page, int size, String keyword, CarStatus status);

    CarResponse getCarDetail(int carId);

    CarRequest getCarUpdateForm(int carId);

    void createCar(CarRequest request);

    void updateCar(int carId, CarRequest request);

    void deleteCar(int carId);

    List<CarProducer> getAllProducers();
}

