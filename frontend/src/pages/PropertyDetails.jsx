import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { getProperty, getReviews, getSeasonalPrices, createBooking, createReview } from '../api/services'
import { useAuth } from '../context/AuthContext.jsx'
import Lightbox from '../components/Lightbox.jsx'
import { PROPERTY_STATUS_CLASS, PROPERTY_STATUS_LABEL } from '../constants/status.js'

export default function PropertyDetails() {
  const { id } = useParams()
  const { user } = useAuth()
  const [n, setN] = useState(null)
  const [loadError, setLoadError] = useState('')
  const [reviews, setReviews] = useState([])
  const [seasonalPrices, setSeasonalPrices] = useState([])
  const [message, setMessage] = useState({ text: '', type: '' })
  // indeks otvorene slike u lightbox-u (null = zatvoren)
  const [openImage, setOpenImage] = useState(null)

  // zahtev za zakup
  const [booking, setBooking] = useState({ dateFrom: '', dateTo: '', message: '' })
  // recenzija
  const [newReview, setNewReview] = useState({ rating: 5, comment: '' })

  const load = async () => {
    setLoadError('')
    try {
      const res = await getProperty(id)
      setN(res.data)
      const r = await getReviews(id)
      setReviews(r.data)
      const c = await getSeasonalPrices(id)
      setSeasonalPrices(c.data)
    } catch (err) {
      setLoadError(err.response?.status === 404
        ? 'Nekretnina nije pronadjena.'
        : (err.response?.data?.message || 'Greska pri ucitavanju nekretnine.'))
    }
  }

  useEffect(() => { load() }, [id])

  const submitBooking = async (e) => {
    e.preventDefault()
    try {
      await createBooking({ userId: user.id, propertyId: Number(id), ...booking })
      setMessage({ text: 'Zahtev je uspesno poslat!', type: 'success' })
      setBooking({ dateFrom: '', dateTo: '', message: '' })
    } catch (err) {
      setMessage({ text: err.response?.data?.message || 'Greska pri slanju zahteva.', type: 'error' })
    }
  }

  const submitReview = async (e) => {
    e.preventDefault()
    try {
      await createReview({ userId: user.id, propertyId: Number(id), rating: Number(newReview.rating), comment: newReview.comment })
      setMessage({ text: 'Recenzija poslata! Bice vidljiva nakon odobrenja administratora.', type: 'success' })
      setNewReview({ rating: 5, comment: '' })
    } catch (err) {
      setMessage({ text: err.response?.data?.message || 'Greska pri slanju recenzije.', type: 'error' })
    }
  }

  if (loadError) return <p className="error">{loadError}</p>
  if (!n) return <p>Ucitavanje...</p>

  // slike za galeriju (fallback ako nekretnina nema nijednu)
  const images = n.images?.length ? n.images : [{ url: 'https://picsum.photos/seed/nekr/800/500' }]

  return (
    <div>
      <h1>
        {n.name}
        <span className={`badge ${PROPERTY_STATUS_CLASS[n.status]}`}>{PROPERTY_STATUS_LABEL[n.status]}</span>
      </h1>
      <p className="muted">{n.address}, {n.city} · {n.type?.name}</p>

      <div className="duo">
        <div>
          {/* galerija - klik na sliku otvara uvecani prikaz */}
          <div className="gallery">
            {images.map((s, i) =>
              <img key={i} src={s.url} alt={`${n.name} - slika ${i + 1}`}
                onClick={() => setOpenImage(i)} />
            )}
          </div>

          <div className="panel">
            <p style={{ marginTop: 0, lineHeight: 1.6 }}>{n.description}</p>
            <ul style={{ lineHeight: 1.8, paddingLeft: 18 }}>
              <li><b>Kvadratura:</b> {n.area} m²</li>
              <li><b>Broj soba:</b> {n.rooms}</li>
              <li><b>Sprat:</b> {n.floor}</li>
              <li><b>Cena zakupa:</b> {n.rentPrice} € / mesec</li>
              <li><b>Depozit:</b> {n.deposit} €</li>
            </ul>
            <div className="amenity-list">
              {n.amenities?.map(p => <span key={p.id} className="amenity-tag">{p.name}</span>)}
            </div>
          </div>

          {seasonalPrices.length > 0 &&
            <div className="panel">
              <h2>Sezonske cene</h2>
              <table>
                <thead>
                  <tr><th>Sezona</th><th>Od</th><th>Do</th><th>Cena / mesec</th></tr>
                </thead>
                <tbody>
                  {seasonalPrices.map(sc =>
                    <tr key={sc.id}>
                      <td>{sc.name}</td>
                      <td>{sc.startDate}</td>
                      <td>{sc.endDate}</td>
                      <td>{sc.price} €</td>
                    </tr>
                  )}
                </tbody>
              </table>
              <p className="muted" style={{ marginBottom: 0 }}>
                Ako period zakupa pada u sezonu, za te dane se primenjuje sezonska cena.
              </p>
            </div>
          }

          <div className="panel">
            <h2>Recenzije {n.reviewCount > 0 && <span className="stars">★ {n.averageRating} ({n.reviewCount})</span>}</h2>
            {reviews.length === 0 && <p>Jos nema odobrenih recenzija.</p>}
            {reviews.map(r =>
              <div key={r.id} style={{ borderBottom: '1px solid #eee', padding: '10px 0' }}>
                <div className="stars">{'★'.repeat(r.rating)}{'☆'.repeat(5 - r.rating)}</div>
                <b>{r.userFullName}</b> <span className="muted">· {r.date}</span>
                <p style={{ margin: '6px 0 0' }}>{r.comment}</p>
              </div>
            )}
          </div>
        </div>

        <div>
          {message.text && <p className={message.type}>{message.text}</p>}

          {user ? (
            <>
              <div className="panel">
                <h2>Posalji zahtev za zakup</h2>
                <form onSubmit={submitBooking}>
                  <label>Period od
                    <input type="date" required value={booking.dateFrom}
                      onChange={e => setBooking({ ...booking, dateFrom: e.target.value })} />
                  </label>
                  <label>Period do
                    <input type="date" required value={booking.dateTo}
                      onChange={e => setBooking({ ...booking, dateTo: e.target.value })} />
                  </label>
                  <label>Poruka
                    <textarea rows="3" value={booking.message}
                      onChange={e => setBooking({ ...booking, message: e.target.value })} />
                  </label>
                  <button type="submit">Posalji zahtev</button>
                  <p className="muted" style={{ fontSize: 12, marginBottom: 0 }}>
                    Ukupna cena se obracunava po danu (mesecna cena / 30); ako period pada u
                    sezonu, primenjuje se sezonska cena.
                  </p>
                </form>
              </div>

              <div className="panel">
                <h2>Ostavi recenziju</h2>
                <form onSubmit={submitReview}>
                  <label>Ocena
                    <select value={newReview.rating} onChange={e => setNewReview({ ...newReview, rating: e.target.value })}>
                      {[5, 4, 3, 2, 1].map(o => <option key={o} value={o}>{o} ★</option>)}
                    </select>
                  </label>
                  <label>Komentar
                    <textarea rows="3" value={newReview.comment}
                      onChange={e => setNewReview({ ...newReview, comment: e.target.value })} />
                  </label>
                  <button type="submit">Posalji recenziju</button>
                </form>
              </div>
            </>
          ) : (
            <div className="panel">
              <p>Prijavite se da biste poslali zahtev za zakup ili ostavili recenziju.</p>
            </div>
          )}
        </div>
      </div>

      {/* uvecani prikaz slike */}
      {openImage !== null &&
        <Lightbox
          images={images}
          index={openImage}
          onClose={() => setOpenImage(null)}
          onChange={setOpenImage}
        />
      }
    </div>
  )
}
