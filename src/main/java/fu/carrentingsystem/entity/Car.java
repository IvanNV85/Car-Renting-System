package fu.carrentingsystem.entity;

import fu.carrentingsystem.constant.CarStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name = "car")
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    int carId;

    @Column(name = "car_name")
    String carName;

    @Column(name = "car_model_year")
    int carModelYear;

    @Column(name = "color")
    String color;

    @Column(name = "capacity")
    int capacity;

    @Column(name = "description")
    String description;

    @Column(name = "import_date")
    Date importDate;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producer_id", nullable = false)
    CarProducer carProducer;

    @Column(name = "rent_price")
    int rentPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    CarStatus status;
}
