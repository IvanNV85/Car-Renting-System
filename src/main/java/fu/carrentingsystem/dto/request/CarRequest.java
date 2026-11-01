package fu.carrentingsystem.dto.request;

import fu.carrentingsystem.constant.CarStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarRequest {

    @NotBlank(message = "Tên xe không được để trống")
    @Size(max = 100, message = "Tên xe tối đa 100 ký tự")
    String carName;

    @NotNull(message = "Năm sản xuất không được để trống")
    @Min(value = 1980, message = "Năm sản xuất tối thiểu là 1980")
    @Max(value = 2100, message = "Năm sản xuất không hợp lệ")
    Integer carModelYear;

    @NotBlank(message = "Màu sắc không được để trống")
    @Size(max = 50, message = "Màu sắc tối đa 50 ký tự")
    String color;

    @NotNull(message = "Số chỗ ngồi không được để trống")
    @Min(value = 2, message = "Số chỗ ngồi tối thiểu là 2")
    @Max(value = 100, message = "Số chỗ ngồi tối đa là 100")
    Integer capacity;

    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    String description;

    @NotNull(message = "Ngày nhập xe không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"dd/MM/yyyy"})
    Date importDate;

    @NotNull(message = "Vui lòng chọn hãng sản xuất")
    Integer producerId;

    @NotNull(message = "Giá thuê theo ngày không được để trống")
    @Min(value = 0, message = "Giá thuê phải lớn hơn hoặc bằng 0")
    Integer rentPrice;

    @NotNull(message = "Vui lòng chọn trạng thái xe")
    CarStatus status;
}

