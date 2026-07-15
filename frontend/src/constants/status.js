// Statusi u backendu su enumi na engleskom (AVAILABLE, SENT, ...),
// a korisniku se prikazuju srpske oznake.

export const PROPERTY_STATUS_LABEL = {
  AVAILABLE: 'Dostupna',
  RESERVED: 'Rezervisana',
  RENTED: 'Izdata',
  WITHDRAWN: 'Povucena'
}

export const PROPERTY_STATUS_CLASS = {
  AVAILABLE: 'b-available',
  RESERVED: 'b-reserved',
  RENTED: 'b-rented',
  WITHDRAWN: 'b-withdrawn'
}

export const BOOKING_STATUS_LABEL = {
  SENT: 'Poslat',
  ACCEPTED: 'Prihvacen',
  REJECTED: 'Odbijen',
  CANCELLED: 'Otkazan'
}
