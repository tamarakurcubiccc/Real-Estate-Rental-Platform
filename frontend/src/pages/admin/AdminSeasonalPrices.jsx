import { useEffect, useState } from 'react'
import {
  getProperties, getSeasonalPrices, addSeason, updateSeason, deleteSeason
} from '../../api/services'

const emptyForm = { name: '', startDate: '', endDate: '', price: '' }

export default function AdminSeasonalPrices() {
  const [properties, setProperties] = useState([])
  const [selectedId, setSelectedId] = useState('')
  const [seasons, setSeasons] = useState([])
  const [form, setForm] = useState(emptyForm)
  const [editId, setEditId] = useState(null)
  const [message, setMessage] = useState('')

  useEffect(() => {
    getProperties({ size: 50 }).then(r => {
      const list = r.data.content
      setProperties(list)
      // podrazumevano izaberi prvu nekretninu
      if (list.length > 0) setSelectedId(String(list[0].id))
    })
  }, [])

  // svaka promena izabrane nekretnine ponovo ucitava njene sezone
  useEffect(() => {
    if (!selectedId) return
    loadSeasons(selectedId)
    cancelIzmenu()
  }, [selectedId])

  const loadSeasons = (id) => getSeasonalPrices(id).then(r => setSeasons(r.data))

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const cancelIzmenu = () => { setForm(emptyForm); setEditId(null) }

  const save = async (e) => {
    e.preventDefault()
    setMessage('')
    const payload = { ...form, price: Number(form.price) }
    try {
      if (editId) await updateSeason(selectedId, editId, payload)
      else await addSeason(selectedId, payload)
      cancelIzmenu()
      loadSeasons(selectedId)
      setMessage('Sacuvano.')
    } catch (err) {
      const d = err.response?.data
      setMessage(d?.validationErrors ? Object.values(d.validationErrors).join(' ') : (d?.message || 'Greska.'))
    }
  }

  const startEdit = (s) => {
    setEditId(s.id)
    setForm({ name: s.name, startDate: s.startDate, endDate: s.endDate, price: s.price })
  }

  const ukloni = async (id) => {
    if (!confirm('Obrisati ovu sezonu?')) return
    await deleteSeason(selectedId, id)
    loadSeasons(selectedId)
  }

  const selected = properties.find(n => String(n.id) === String(selectedId))

  return (
    <div>
      <h1>Sezonski cenovnik</h1>

      <div className="panel">
        <label>Nekretnina
          <select value={selectedId} onChange={e => setSelectedId(e.target.value)}>
            {properties.map(n => <option key={n.id} value={n.id}>{n.name} ({n.city})</option>)}
          </select>
        </label>
        {selected &&
          <p className="muted" style={{ margin: 0 }}>
            Podrazumevana cena: <b>{selected.rentPrice} € / mesec</b>. Sezonska cena vazi
            samo za dane koji padaju u definisani period.
          </p>
        }
      </div>

      {message && <p className={message === 'Sacuvano.' ? 'success' : 'error'}>{message}</p>}

      <div className="panel">
        <h2>{editId ? 'Izmena sezone' : 'Nova sezona'}</h2>
        <form onSubmit={save}>
          <div className="filters">
            <label>Naziv
              <input name="name" required value={form.name} onChange={handleChange}
                placeholder="npr. Zimska sezona" />
            </label>
            <label>Datum od
              <input type="date" name="startDate" required value={form.startDate} onChange={handleChange} />
            </label>
            <label>Datum do
              <input type="date" name="endDate" required value={form.endDate} onChange={handleChange} />
            </label>
            <label>Cena (€ / mesec)
              <input type="number" name="price" required min="1" value={form.price} onChange={handleChange} />
            </label>
          </div>
          <button type="submit">{editId ? 'Sacuvaj izmene' : 'Dodaj sezonu'}</button>
          {editId &&
            <button type="button" className="btn-sek" style={{ marginLeft: 8 }}
              onClick={cancelIzmenu}>Otkazi</button>
          }
        </form>
      </div>

      <div className="panel">
        <h2>Definisane sezone</h2>
        {seasons.length === 0
          ? <p className="muted">Ova nekretnina nema definisane sezone - vazi podrazumevana cena.</p>
          : <table>
              <thead>
                <tr><th>Sezona</th><th>Od</th><th>Do</th><th>Cena</th><th></th></tr>
              </thead>
              <tbody>
                {seasons.map(s =>
                  <tr key={s.id}>
                    <td>{s.name}</td>
                    <td>{s.startDate}</td>
                    <td>{s.endDate}</td>
                    <td>{s.price} €</td>
                    <td style={{ whiteSpace: 'nowrap' }}>
                      <button className="btn-sek" onClick={() => startEdit(s)}>Izmeni</button>
                      <button className="btn-opasno" style={{ marginLeft: 6 }}
                        onClick={() => ukloni(s.id)}>Obrisi</button>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
        }
      </div>
    </div>
  )
}
