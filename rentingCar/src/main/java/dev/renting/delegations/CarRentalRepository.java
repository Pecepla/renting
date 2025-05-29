package dev.renting.delegations;

import dev.renting.users.Booking;

import java.time.LocalDate;
import java.util.List;

public interface CarRentalRepository  {

   List<Car> getAvailableCars();

    <T> T getUniqueDelegations();

    List <Delegation> getCarsByDelegation(String delegationId);

    List <Booking> getBookingsByDelegationAndDates(String delegationId, LocalDate start, LocalDate end);

    <T> void  markCarAsRented();

    Booking createBooking();
}
