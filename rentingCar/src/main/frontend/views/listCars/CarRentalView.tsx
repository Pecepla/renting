import { DatePicker } from '@vaadin/react-components/DatePicker';
 import { useEffect, useState } from 'react';
 import { CarRentalEndpoint } from 'Frontend/generated/endpoints';

 export default function CarCalendar() {
   const [delegation, setDelegation] = useState<string>('');
   const [startDate, setStartDate] = useState<LocalDate>();
   const [endDate, setEndDate] = useState<LocalDate>();
   const [availableCars, setAvailableCars] = useState<Car[]>([]);

   useEffect(() => {
    if (delegation && startDate && endDate) {
       CarRentalEndpoint.getAvailableCars(delegation, startDate, endDate)
         .then(cars => setAvailableCars(cars));
     }
   }, [delegation, startDate, endDate]);

   return (
     <div className="p-m">
       <ComboBox
         label="Delegación"
         items={['DELEG#001', 'DELEG#002']}
         onValueChanged={e => setDelegation(e.detail.value)}
       />

       <DatePicker
         label="Fecha inicio"
         onValueChanged={e => setStartDate(e.detail.value)}
       />

       <DatePicker
         label="Fecha fin"
         min={startDate}
         onValueChanged={e => setEndDate(e.detail.value)}
       />

       <Grid items={availableCars}>
         <GridColumn path="make" header="Marca" />
         <GridColumn path="model" header="Modelo" />
         <GridColumn>
           {({ item }) => (
             <Button
               onClick={() => handleBooking(item.operation)}
               disabled={!startDate || !endDate}
             >
               Reservar
             </Button>
           )}
         </GridColumn>
       </Grid>
     </div>
   );

   async function handleBooking(carOperation: string) {
     try {
       await CarRentalEndpoint.createBooking(
         'currentUser',
         carOperation,
         startDate!,
         endDate!
       );
       Notification.show('¡Reserva confirmada!');
     } catch (e) {
       Notification.show(`Error: ${e.message}`);
     }
   }
 }
