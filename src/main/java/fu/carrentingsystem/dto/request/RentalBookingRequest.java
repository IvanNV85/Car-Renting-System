package fu.carrentingsystem.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentalBookingRequest {

    @NotEmpty(message = "Vui lòng chọn ít nhất một xe để đặt thuê")
    @Builder.Default
    List<Integer> carIds = new ArrayList<>();

    @NotNull(message = "Vui lòng chọn ngày nhận xe")
    @DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"dd/MM/yyyy"})
    LocalDate pickupDate;

    @NotNull(message = "Vui lòng chọn ngày trả xe")
    @DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"dd/MM/yyyy"})
    LocalDate returnDate;

    String note;
}

