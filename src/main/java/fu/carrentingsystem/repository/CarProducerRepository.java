package fu.carrentingsystem.repository;

import fu.carrentingsystem.entity.CarProducer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarProducerRepository extends JpaRepository<CarProducer, Integer> {
}

