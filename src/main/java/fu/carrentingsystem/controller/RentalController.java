package fu.carrentingsystem.controller;

import fu.carrentingsystem.constant.CarRentalStatus;
import fu.carrentingsystem.dto.request.RentalBookingRequest;
import fu.carrentingsystem.dto.response.CarRentalResponse;
import fu.carrentingsystem.dto.response.CarResponse;
import fu.carrentingsystem.service.CarRentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RentalController {

    private final CarRentalService carRentalService;

    @GetMapping("/rentals/new")
    @PreAuthorize("isAuthenticated()")
    public String showBookingForm(
            @RequestParam(value = "carId", required = false) Integer carId,
            Model model) {

        List<CarResponse> availableCars = carRentalService.getAvailableCarsForBooking();

        RentalBookingRequest bookingRequest = new RentalBookingRequest();
        bookingRequest.setPickupDate(LocalDate.now().plusDays(1));
        bookingRequest.setReturnDate(LocalDate.now().plusDays(3));

        if (carId != null) {
            bookingRequest.getCarIds().add(carId);
        }

        model.addAttribute("bookingRequest", bookingRequest);
        model.addAttribute("availableCars", availableCars);
        model.addAttribute("selectedCarId", carId);

        return "rentals/booking";
    }

    @PostMapping("/rentals/new")
    @PreAuthorize("isAuthenticated()")
    public String processBooking(
            @Valid @ModelAttribute("bookingRequest") RentalBookingRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("availableCars", carRentalService.getAvailableCarsForBooking());
            return "rentals/booking";
        }

        try {
            carRentalService.createRentalTransaction(authentication.getName(), request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đặt thuê xe thành công! Bạn có thể xem thông tin chi tiết các hợp đồng thuê dưới đây.");
            return "redirect:/my-rentals";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("availableCars", carRentalService.getAvailableCarsForBooking());
            return "rentals/booking";
        }
    }

    @GetMapping("/my-rentals")
    @PreAuthorize("isAuthenticated()")
    public String showRentalHistory(Model model, Authentication authentication) {
        List<CarRentalResponse> rentals = carRentalService.getCustomerRentalHistory(authentication.getName());

        long activeRentals = rentals.stream()
                .filter(r -> r.getStatus() == CarRentalStatus.ACTIVE)
                .count();

        BigDecimal totalSpent = rentals.stream()
                .filter(r -> r.getStatus() != CarRentalStatus.CANCELED)
                .map(CarRentalResponse::getRentPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("rentals", rentals);
        model.addAttribute("totalRentals", rentals.size());
        model.addAttribute("activeRentals", activeRentals);
        model.addAttribute("totalSpent", totalSpent);

        return "rentals/history";
    }

    @PostMapping("/my-rentals/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public String cancelRental(
            @PathVariable("id") int id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            carRentalService.cancelRental(id, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Hủy hợp đồng thuê xe #" + id + " thành công! Xe đã được hoàn trả về trạng thái sẵn sàng phục vụ.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/my-rentals";
    }
}

