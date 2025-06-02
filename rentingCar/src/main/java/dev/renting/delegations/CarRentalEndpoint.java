package dev.renting.delegations;
import com.vaadin.hilla.Endpoint;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import dev.renting.users.Booking;


@Endpoint
@AnonymousAllowed
public class CarRentalEndpoint {

    private final CarRentalRepository carRentalRepository;

    @Autowired
    public CarRentalEndpoint(CarRentalRepository carRentalRepository) {
        this.carRentalRepository = carRentalRepository;
    }

    public List<Car> getAvailableCars(String delegationId, LocalDate start, LocalDate end)  {
        List<Car> cars = carRentalRepository.getCarsByDelegation(delegationId).stream()
                .map(delegation -> {
                    Car car = new Car();
                    car.setOperation(delegation.getOperation());
                    car.setMake(delegation.getMake());
                    car.setModel(delegation.getModel());
                    return car;
                })
                .collect(Collectors.toList());
        List<Booking> bookings = carRentalRepository.getBookingsByDelegationAndDates(delegationId, start, end);

        Set<String> bookedCarOps = bookings.stream()
                .map(Booking::getOperation)
                .collect(Collectors.toSet());

        return cars.stream()
                .filter(car -> !bookedCarOps.contains(car.getOperation()))
                .collect(Collectors.toList());
    }

    public List<String> getUniqueDelegations() {
        return carRentalRepository.getUniqueDelegations();
    }

    public List<Car> getCarsByDelegation(String delegationId) {
        return carRentalRepository.getCarsByDelegation(delegationId).stream()
                .map(delegation -> {
                    Car car = new Car();
                    car.setOperation(delegation.getOperation());
                    car.setMake(delegation.getMake());
                    car.setModel(delegation.getModel());
                    return car;
                })
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByDelegationAndDates(String delegationId, LocalDate start, LocalDate end) {
        return carRentalRepository.getBookingsByDelegationAndDates(delegationId, start, end);
    }

    public Booking createBooking(String userId, String carOperation, LocalDate start, LocalDate end, String delegationId) {
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setCar(carOperation);
        booking.setStartDate(String.valueOf(start));
        booking.setEndDate(String.valueOf(end));
        booking.setDelegationId(delegationId);
        return carRentalRepository.createBooking(booking);
    }

    public void markCarAsRented(String carOperation, boolean rented) {
        // Implement if needed; currently unused
        carRentalRepository.markCarAsRented(carOperation);
    }
}