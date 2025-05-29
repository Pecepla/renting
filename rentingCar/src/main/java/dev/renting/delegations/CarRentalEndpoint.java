package dev.renting.delegations;

import com.vaadin.hilla.Endpoint;
import dev.renting.users.Booking;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@Endpoint
@AnonymousAllowed
public class CarRentalEndpoint{

    private final CarRentalRepository carRentalRepository;


    @Autowired
    public CarRentalEndpoint(CarRentalRepository carRentalRepository) {
        this.carRentalRepository = carRentalRepository;
    }


    private Object getAvailableCars() {
        return this.carRentalRepository.getAvailableCars();
    }

    public List<String> getUniqueDelegations() {
        return this.carRentalRepository.getUniqueDelegations();
    }

    public List<Delegation> getCarsByDelegation(String delegationId) {
        return this.carRentalRepository.getCarsByDelegation(delegationId);
    }

    public List<Booking> getBookingsByDelegationAndDates(String delegationId, LocalDate start, LocalDate end) {
        return this.carRentalRepository.getBookingsByDelegationAndDates(delegationId,start, end);
    }

    public Booking createBooking(String userId, String carOperation, LocalDate start, LocalDate end, String delegationId) {
        return this.carRentalRepository.createBooking();
    }

    public void markCarAsRented(String carOperation, boolean b) {
        this.carRentalRepository.markCarAsRented();
    }
}




