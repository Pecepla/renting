package dev.renting.delegations;


import dev.renting.users.Booking;

import java.time.LocalDate;
import java.util.List;

public interface CarRentalRepository {

 List<Car> getAvailableCars(String delegationId, LocalDate start, LocalDate end);

 List<String> getUniqueDelegations();

List <Car> getCarsByDelegation(String delegationId);

 List<Booking> getBookingsByDelegationAndDates(String delegationId, LocalDate start, LocalDate end);

 void markCarAsRented(String carOperation);

 Booking createBooking(Booking booking);
}