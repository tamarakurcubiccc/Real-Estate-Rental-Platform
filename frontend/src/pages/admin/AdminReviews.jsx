import { useEffect, useState } from 'react'
import { getPendingReviews, approveReview, deleteReview } from '../../api/services'

export default function AdminReviews() {
  const [reviews, setReviews] = useState([])

  const load = () => getPendingReviews().then(r => setReviews(r.data))
  useEffect(() => { load() }, [])

  const approve = async (id) => { await approveReview(id); load() }
  const remove = async (id) => { if (confirm('Obrisati recenziju?')) { await deleteReview(id); load() } }

  return (
    <div>
      <h1>Moderacija recenzija</h1>
      <p className="muted">Recenzije koje cekaju odobrenje.</p>
      <div className="panel">
        <table>
          <thead>
            <tr><th>Korisnik</th><th>Nekretnina</th><th>Ocena</th><th>Komentar</th><th>Akcije</th></tr>
          </thead>
          <tbody>
            {reviews.map(r =>
              <tr key={r.id}>
                <td>{r.userFullName}</td>
                <td>{r.propertyName}</td>
                <td className="stars">{'★'.repeat(r.rating)}</td>
                <td>{r.comment}</td>
                <td style={{ display: 'flex', gap: 6 }}>
                  <button className="btn-ok" onClick={() => approve(r.id)}>Odobri</button>
                  <button className="btn-opasno" onClick={() => remove(r.id)}>Obrisi</button>
                </td>
              </tr>
            )}
            {reviews.length === 0 && <tr><td colSpan="5">Nema recenzija za moderaciju.</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  )
}
