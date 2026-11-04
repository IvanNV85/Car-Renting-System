package fu.carrentingsystem.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerResponse {
    int customerId;
    String fullName;
    String mobile;
    LocalDate birthday;
    String identityCard;
    String licenceNumber;
    LocalDate licenceDate;
    // from Account
    int accountId;
    String accountName;
    String email;
    String role;
}
