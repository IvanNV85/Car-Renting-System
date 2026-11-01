package fu.carrentingsystem.repository;

import fu.carrentingsystem.constant.CarRentalStatus;
import fu.carrentingsystem.entity.CarRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRentalRepository extends JpaRepository<CarRental, Integer> {
    boolean existsByCustomer_CustomerId(int customerId);

    boolean existsByCar_CarId(int carId);

    boolean existsByCar_CarIdAndStatusIn(int carId, Collection<CarRentalStatus> statuses);

    List<CarRental> findByCustomer_CustomerIdOrderByCarRentIdDesc(int customerId);

    List<CarRental> findByCustomer_Account_EmailOrderByCarRentIdDesc(String email);

    List<CarRental> findByCustomer_Account_AccountNameOrderByCarRentIdDesc(String accountName);

    Optional<CarRental> findByCarRentIdAndCustomer_CustomerId(int carRentId, int customerId);
}
