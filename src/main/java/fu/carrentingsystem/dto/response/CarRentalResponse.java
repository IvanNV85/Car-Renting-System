package fu.carrentingsystem.dto.response;

import fu.carrentingsystem.constant.CarRentalStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarRentalResponse {
    int carRentId;
    int customerId;
    String customerName;
    String customerMobile;
    int carId;
    String carName;
    String producerName;
    int carModelYear;
    String color;
    int capacity;
    LocalDate pickupDate;
    LocalDate returnDate;
    long totalDays;
    BigDecimal rentPrice;
    CarRentalStatus status;
    boolean canCancel;
}

