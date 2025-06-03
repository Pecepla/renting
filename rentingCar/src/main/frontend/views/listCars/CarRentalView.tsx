import { DatePicker } from '@vaadin/react-components/DatePicker';
import { ComboBox } from '@vaadin/react-components/ComboBox';
import { Grid, GridColumn } from '@vaadin/react-components/Grid';
import { Button } from '@vaadin/react-components/Button';
import { Notification } from '@vaadin/react-components/Notification';
import { useEffect, useState } from 'react';
import { CarRentalEndpoint } from 'Frontend/generated/endpoints';
import type { LocalDate } from '@vaadin/date-picker';

interface Car {
  operation: string;
  make: string;
  model: string;
}

export default function CarRentalView() {
  const [delegation, setDelegation] = useState<string>('');
  const [startDate, setStartDate] = useState<LocalDate | undefined>();
  const [endDate, setEndDate] = useState<LocalDate | undefined>();
  const [availableCars, setAvailableCars] = useState<Car[]>([]);
  const [delegations, setDelegations] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  // Fetch unique delegations on mount
  useEffect(() => {
    CarRentalEndpoint.getUniqueDelegations()
      .then(setDelegations)
      .catch((err) => setError(`Failed to load delegations: ${err.message}`));
  }, []);

  // Fetch available cars when delegation or dates change
  useEffect(() => {
    if (delegation && startDate && endDate) {
      if (new Date(endDate) < new Date(startDate)) {
        setError('End date cannot be before start date');
        setAvailableCars([]);
        return;
      }
      setLoading(true);
      setError(null);
      CarRentalEndpoint.getAvailableCars(delegation, startDate, endDate)
        .then((cars) => {
          setAvailableCars(cars);
          if (cars.length === 0) {
            Notification.show('No cars available for the selected criteria', {
              theme: 'warning',
            });
          }
        })
        .catch((err) => setError(`Failed to load cars: ${err.message}`))
        .finally(() => setLoading(false));
    } else {
      setAvailableCars([]);
    }
  }, [delegation, startDate, endDate]);

  async function handleBooking(carOperation: string) {
    if (!startDate || !endDate || !delegation) {
      Notification.show('Please select delegation and dates', { theme: 'error' });
      return;
    }
    try {
      setLoading(true);
      await CarRentalEndpoint.createBooking('currentUser', carOperation, startDate, endDate, delegation);
      Notification.show('Booking confirmed!', { theme: 'success' });
      // Refresh available cars
      const cars = await CarRentalEndpoint.getAvailableCars(delegation, startDate, endDate);
      setAvailableCars(cars);
    } catch (e: any) {
      Notification.show(`Booking failed: ${e.message}`, { theme: 'error' });
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="p-m flex flex-col gap-m">
      <h2>Car Rental</h2>
      {error && <div className="text-error">{error}</div>}
      <ComboBox
        label="Delegation"
        items={delegations}
        value={delegation}
        onValueChanged={(e) => setDelegation(e.detail.value)}
        disabled={loading}
      />
      <div className="flex gap-s">
        <DatePicker
          label="Start Date"
          value={startDate}
          onValueChanged={(e) => setStartDate(e.detail.value)}
          disabled={loading}
        />
        <DatePicker
          label="End Date"
          value={endDate}
          min={startDate}
          onValueChanged={(e) => setEndDate(e.detail.value)}
          disabled={loading}
        />
      </div>
      {loading ? (
        <div>Loading cars...</div>
      ) : (
        <Grid items={availableCars}>
          <GridColumn path="make" header="Make" />
          <GridColumn path="model" header="Model" />
          <GridColumn header="Action">
            {({ item }: { item: Car }) => (
              <Button
                onClick={() => handleBooking(item.operation)}
                disabled={loading || !startDate || !endDate}
                theme="primary"
              >
                Book
              </Button>
            )}
          </GridColumn>
        </Grid>
      )}
    </div>
  );
}