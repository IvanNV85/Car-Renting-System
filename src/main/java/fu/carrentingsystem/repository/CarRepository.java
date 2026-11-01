package fu.carrentingsystem.repository;

import fu.carrentingsystem.constant.CarStatus;
import fu.carrentingsystem.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {

    List<Car> findByStatus(CarStatus status);

    Page<Car> findByCarNameContainingIgnoreCase(String carName, Pageable pageable);

    Page<Car> findByStatus(CarStatus status, Pageable pageable);

    Page<Car> findByCarNameContainingIgnoreCaseAndStatus(String carName, CarStatus status, Pageable pageable);
}
