package fu.carrentingsystem;

import fu.carrentingsystem.constant.CarRentalStatus;
import fu.carrentingsystem.constant.CarStatus;
import fu.carrentingsystem.dto.response.CarRentalResponse;
import fu.carrentingsystem.dto.response.CarResponse;
import fu.carrentingsystem.service.CarRentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class RentalControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private CarRentalService carRentalService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testBookingPageRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/rentals/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/login")));
    }

    @Test
    void testHistoryPageRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/my-rentals"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/login")));
    }

    @Test
    @WithMockUser(username = "customer@fu.edu.vn", roles = "CUSTOMER")
    void testShowBookingForm() throws Exception {
        CarResponse mockCar = CarResponse.builder()
                .carId(1)
                .carName("Toyota Corolla")
                .carModelYear(2022)
                .color("White")
                .capacity(5)
                .importDate(new Date())
                .producerId(1)
                .producerName("Toyota")
                .rentPrice(800000)
                .status(CarStatus.AVAILABLE)
                .build();

        when(carRentalService.getAvailableCarsForBooking()).thenReturn(List.of(mockCar));

        mockMvc.perform(get("/rentals/new?carId=1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rentals/booking"))
                .andExpect(model().attributeExists("bookingRequest", "availableCars", "selectedCarId"));
    }

    @Test
    @WithMockUser(username = "customer@fu.edu.vn", roles = "CUSTOMER")
    void testShowRentalHistory() throws Exception {
        CarRentalResponse mockRental = CarRentalResponse.builder()
                .carRentId(1)
                .customerId(1)
                .customerName("Nguyen Van A")
                .customerMobile("0912345678")
                .carId(1)
                .carName("Toyota Camry")
                .producerName("Toyota")
                .carModelYear(2022)
                .color("Trắng")
                .capacity(5)
                .pickupDate(LocalDate.now().plusDays(2))
                .returnDate(LocalDate.now().plusDays(5))
                .totalDays(3)
                .rentPrice(BigDecimal.valueOf(2400000))
                .status(CarRentalStatus.ACTIVE)
                .canCancel(true)
                .build();

        when(carRentalService.getCustomerRentalHistory("customer@fu.edu.vn"))
                .thenReturn(List.of(mockRental));

        mockMvc.perform(get("/my-rentals"))
                .andExpect(status().isOk())
                .andExpect(view().name("rentals/history"))
                .andExpect(model().attributeExists("rentals", "totalRentals", "activeRentals", "totalSpent"));
    }

    @Test
    @WithMockUser(username = "customer@fu.edu.vn", roles = "CUSTOMER")
    void testProcessBookingSuccess() throws Exception {
        doNothing().when(carRentalService).createRentalTransaction(eq("customer@fu.edu.vn"), any());

        mockMvc.perform(post("/rentals/new")
                        .with(csrf())
                        .param("carIds", "1", "2")
                        .param("pickupDate", LocalDate.now().plusDays(1).toString())
                        .param("returnDate", LocalDate.now().plusDays(4).toString())
                        .param("note", "Giao xe tận nơi"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-rentals"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(username = "customer@fu.edu.vn", roles = "CUSTOMER")
    void testCancelRental() throws Exception {
        doNothing().when(carRentalService).cancelRental(eq(1), eq("customer@fu.edu.vn"));

        mockMvc.perform(post("/my-rentals/1/cancel").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-rentals"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}

