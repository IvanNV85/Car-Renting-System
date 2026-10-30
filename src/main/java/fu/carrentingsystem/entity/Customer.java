package fu.carrentingsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "customer")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "account")
@EqualsAndHashCode(exclude = "account")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    int customerId;

    @Column(name = "full_name", nullable = false, length = 200)
    String fullName;

    @Column(name = "mobile", nullable = false, length = 15)
    String mobile;

    @Column(name = "birthday", nullable = false)
    LocalDate birthday;

    @Column(name = "identity_card", nullable = false, length = 20)
    String identityCard;

    @Column(name = "licence_number", nullable = false, length = 20)
    String licenceNumber;

    @Column(name = "licence_date", nullable = false)
    LocalDate licenceDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    Account account;
}
