package dev.renting.delegations;


import dev.renting.users.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public abstract class CarRentalRepositoryImpl implements CarRentalRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<Car> carTable;
    private final DynamoDbTable<Booking> bookingTable;
    private final DynamoDbTable<Delegation> delegationTable;

    @Autowired
    public CarRentalRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
        this.carTable = enhancedClient.table("Car", TableSchema.fromBean(Car.class));
        this.bookingTable = enhancedClient.table("Booking", TableSchema.fromBean(Booking.class));
        this.delegationTable = enhancedClient.table("Delegation", TableSchema.fromBean(Delegation.class));
    }

    @Override
    public List<Car> getAvailableCars() {
        return carTable.scan().items().stream().collect(Collectors.toList());
    }

    @Override
    public List<String> getUniqueDelegations() {
        return delegationTable.scan().items()
                .stream()
                .map(Delegation::getDelegationId)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Object getCarsByDelegation(String delegationId) {
        return carTable.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(delegationId).build()))
                .items()
                .stream()
                .map(car -> {
                    Delegation delegation = new Delegation();
                    delegation.setDelegationId(delegationId);
                    delegation.setOperation(car.getOperation());
                    delegation.setMake(car.getMake());
                    delegation.getModel();
                    return delegation;
                })
                .collect(Collectors.toList()).reversed();
    }

    @Override
    public List<Booking> getBookingsByDelegationAndDates(String delegationId, LocalDate start, LocalDate end) {
        return bookingTable.scan().items()
                .stream()
                .filter(booking -> booking.getDelegationId().equals(delegationId)
                        && !booking.getEndDate().equals(start)
                        && !booking.getStartDate().equals(end))
                .collect(Collectors.toList());
    }


}
