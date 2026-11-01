package fu.carrentingsystem.service;

import fu.carrentingsystem.dto.request.RentalBookingRequest;
import fu.carrentingsystem.dto.response.CarRentalResponse;
import fu.carrentingsystem.dto.response.CarResponse;

import java.util.List;

public interface CarRentalService {

    void createRentalTransaction(String usernameOrEmail, RentalBookingRequest request);

    List<CarRentalResponse> getCustomerRentalHistory(String usernameOrEmail);

    CarRentalResponse getRentalDetail(int carRentId, String usernameOrEmail);

    void cancelRental(int carRentId, String usernameOrEmail);

    List<CarResponse> getAvailableCarsForBooking();
}

