import { useEffect, useState } from 'react'
import { getMyBookings, cancelBooking } from '../api/services'
import { useAuth } from '../context/AuthContext.jsx'
import { BOOKING_STATUS_LABEL } from '../constants/status.js'

export default function MyBookings() {
  const { user } = useAuth()
  const [bookings, setBookings] = useState([])

  const load = () => getMyBookings(user.id).then(r => setBookings(r.data))
  useEffect(() => { load() }, [])

  const cancel = async (id) => {
    try { await cancelBooking(id, user.id); load() }
    catch (err) { alert(err.response?.data?.message || 'Greska.') }
  }

  return (
    <div>
      <h1>Moji zahtevi</h1>
      <div className="panel">
        <table>
          <thead>
            <tr><th>Nekretnina</th><th>Period</th><th>Ukupna cena</th><th>Status</th><th></th></tr>
          </thead>
          <tbody>
            {bookings.map(z =>
              <tr key={z.id}>
                <td>{z.propertyName}</td>
                <td>{z.dateFrom} – {z.dateTo}</td>
                <td>{z.totalPrice} €</td>
                <td>{BOOKING_STATUS_LABEL[z.status]}</td>
                <td>{z.status === 'SENT' &&
                  <button className="btn-opasno" onClick={() => cancel(z.id)}>Otkazi</button>}</td>
              </tr>
            )}
            {bookings.length === 0 && <tr><td colSpan="5">Nemate poslatih zahteva.</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  )
}
