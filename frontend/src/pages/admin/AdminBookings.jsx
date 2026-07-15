import { useEffect, useState } from 'react'
import { getBookings, changeBookingStatus } from '../../api/services'
import { BOOKING_STATUS_LABEL } from '../../constants/status.js'

export default function AdminBookings() {
  const [bookings, setBookings] = useState([])

  const load = () => getBookings().then(r => setBookings(r.data))
  useEffect(() => { load() }, [])

  const change = async (id, status) => {
    try { await changeBookingStatus(id, status); load() }
    catch (err) { alert(err.response?.data?.message || 'Greska.') }
  }

  return (
    <div>
      <h1>Zahtevi za zakup</h1>
      <div className="panel">
        <table>
          <thead>
            <tr><th>Korisnik</th><th>Nekretnina</th><th>Period</th><th>Poruka</th><th>Ukupna cena</th><th>Status</th><th>Akcije</th></tr>
          </thead>
          <tbody>
            {bookings.map(z =>
              <tr key={z.id}>
                <td>{z.userFullName}</td>
                <td>{z.propertyName}</td>
                <td>{z.dateFrom} – {z.dateTo}</td>
                <td style={{ maxWidth: 260, whiteSpace: 'pre-wrap' }}>
                  {z.message ? z.message : <span className="muted">—</span>}
                </td>
                <td>{z.totalPrice} €</td>
                <td>{BOOKING_STATUS_LABEL[z.status]}</td>
                <td style={{ display: 'flex', gap: 6 }}>
                  {z.status === 'SENT' && <>
                    <button className="btn-ok" onClick={() => change(z.id, 'ACCEPTED')}>Prihvati</button>
                    <button className="btn-opasno" onClick={() => change(z.id, 'REJECTED')}>Odbij</button>
                  </>}
                </td>
              </tr>
            )}
            {bookings.length === 0 && <tr><td colSpan="7">Nema zahteva.</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  )
}
