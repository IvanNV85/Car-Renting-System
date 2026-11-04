package fu.carrentingsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerUpdateRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 200, message = "Họ và tên tối đa 200 ký tự")
    String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Số điện thoại phải từ 10 đến 15 chữ số")
    String mobile;

    @NotNull(message = "Vui lòng chọn ngày sinh")
    @Past(message = "Ngày sinh phải ở trong quá khứ")
    @DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"dd/MM/yyyy"})
    LocalDate birthday;

    @NotBlank(message = "Số CCCD/CMND không được để trống")
    @Size(min = 9, max = 20, message = "Số CCCD/CMND phải từ 9 đến 20 ký tự")
    String identityCard;

    @NotBlank(message = "Số giấy phép lái xe không được để trống")
    @Size(min = 8, max = 20, message = "Số GPLX phải từ 8 đến 20 ký tự")
    String licenceNumber;

    @NotNull(message = "Vui lòng chọn ngày cấp GPLX")
    @Past(message = "Ngày cấp GPLX phải ở trong quá khứ")
    @DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"dd/MM/yyyy"})
    LocalDate licenceDate;
}
