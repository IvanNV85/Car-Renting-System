package fu.carrentingsystem.entity;

import fu.carrentingsystem.constant.CarRentalStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "car_rental")
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class CarRental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // auto increment
    @Column(name = "car_rent_id")
    int carRentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    Car car;

    @Column(name = "pickup_date", nullable = false)
    LocalDate pickupDate;

    @Column(name = "return_date", nullable = false)
    LocalDate returnDate;

    @Column(name = "rent_price", nullable = false, precision = 10, scale = 2)
    BigDecimal rentPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    CarRentalStatus status;

}
