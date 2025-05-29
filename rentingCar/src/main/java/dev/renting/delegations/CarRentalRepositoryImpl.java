package dev.renting.delegations;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.util.List;

@Repository
public abstract class CarRentalRepositoryImpl implements CarRentalRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final String tableName = "car";

    @Autowired
    public CarRentalRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    @Override
    public List<Car> getAvailableCars(){

        return  null;
    }


}
