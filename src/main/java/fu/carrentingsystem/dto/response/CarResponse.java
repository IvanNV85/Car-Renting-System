package fu.carrentingsystem.dto.response;

import fu.carrentingsystem.constant.CarStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarResponse {
    int carId;
    String carName;
    int carModelYear;
    String color;
    int capacity;
    String description;
    Date importDate;
    int producerId;
    String producerName;
    int rentPrice;
    CarStatus status;
}
