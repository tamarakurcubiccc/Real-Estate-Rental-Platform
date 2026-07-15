import { useEffect, useState } from 'react'
import { PROPERTY_STATUS_LABEL } from '../../constants/status.js'
import {
  getProperties, getPropertyTypes, getAmenities,
  createProperty, updateProperty, deleteProperty,
  addImage, deleteImage
} from '../../api/services'

const emptyForm = {
  name: '', address: '', city: '', area: 0, rooms: 0, floor: 0,
  rentPrice: 0, deposit: 0, description: '', typeId: '', amenityIds: []
}

export default function AdminProperties() {
  const [properties, setProperties] = useState([])
  const [types, setTypes] = useState([])
  const [amenities, setAmenities] = useState([])
  const [form, setForm] = useState(emptyForm)
  const [editId, setEditId] = useState(null)
  const [message, setMessage] = useState('')
  // galerija nekretnine koja se trenutno menja
  const [images, setImages] = useState([])
  const [newImage, setNewImage] = useState({ url: '', primary: false })

  // admin pregleda sve nekretnine odjednom (size=50 je maksimum na backendu)
  const load = () => getProperties({ size: 50 }).then(r => setProperties(r.data.content))

  useEffect(() => {
    load()
    getPropertyTypes().then(r => setTypes(r.data))
    getAmenities().then(r => setAmenities(r.data))
  }, [])

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const toggleAmenity = (id) => {
    const ima = form.amenityIds.includes(id)
    setForm({
      ...form,
      amenityIds: ima ? form.amenityIds.filter(x => x !== id) : [...form.amenityIds, id]
    })
  }

  const save = async (e) => {
    e.preventDefault()
    setMessage('')
    const payload = {
      ...form,
      area: Number(form.area), rooms: Number(form.rooms), floor: Number(form.floor),
      rentPrice: Number(form.rentPrice), deposit: Number(form.deposit), typeId: Number(form.typeId)
    }
    try {
      if (editId) await updateProperty(editId, payload)
      else await createProperty(payload)
      setForm(emptyForm); setEditId(null); load()
      setMessage('Sacuvano.')
    } catch (err) {
      const d = err.response?.data
      setMessage(d?.validationErrors ? Object.values(d.validationErrors).join(' ') : (d?.message || 'Greska.'))
    }
  }

  const startEdit = (n) => {
    setEditId(n.id)
    setForm({
      name: n.name, address: n.address || '', city: n.city,
      area: n.area, rooms: n.rooms, floor: n.floor,
      rentPrice: n.rentPrice, deposit: n.deposit || 0, description: n.description || '',
      typeId: n.type?.id || '', amenityIds: n.amenities?.map(p => p.id) || []
    })
    setImages(n.images || [])
    setNewImage({ url: '', primary: false })
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  // --- galerija slika (dostupna tek kad je nekretnina sacuvana) ---
  const submitImage = async (e) => {
    e.preventDefault()
    setMessage('')
    try {
      const res = await addImage(editId, newImage)
      setImages(res.data.images || [])   // backend vraca azuriranu nekretninu
      setNewImage({ url: '', primary: false })
      load()
    } catch (err) {
      setMessage(err.response?.data?.message || 'Greska pri dodavanju slike.')
    }
  }

  const removeImage = async (imageId) => {
    if (!confirm('Obrisati ovu sliku?')) return
    await deleteImage(editId, imageId)
    setImages(images.filter(s => s.id !== imageId))
    load()
  }

  const remove = async (id) => {
    if (!confirm('Obrisati nekretninu?')) return
    await deleteProperty(id); load()
  }


  return (
    <div>
      <h1>Administracija nekretnina</h1>

      <div className="panel">
        <h2>{editId ? 'Izmena nekretnine' : 'Nova nekretnina'}</h2>
        {message && <p className={message === 'Sacuvano.' ? 'success' : 'error'}>{message}</p>}
        <form onSubmit={save}>
          <div className="filters">
            <label>Naziv<input name="name" required value={form.name} onChange={handleChange} /></label>
            <label>Grad<input name="city" required value={form.city} onChange={handleChange} /></label>
            <label>Adresa<input name="address" value={form.address} onChange={handleChange} /></label>
            <label>Tip
              <select name="typeId" required value={form.typeId} onChange={handleChange}>
                <option value="">-- izaberi --</option>
                {types.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
              </select>
            </label>
            <label>Kvadratura (m²)<input name="area" type="number" min="0" step="0.5" value={form.area} onChange={handleChange} /></label>
            <label>Broj soba<input name="rooms" type="number" min="0" step="1" value={form.rooms} onChange={handleChange} /></label>
            <label>Sprat<input name="floor" type="number" step="1" value={form.floor} onChange={handleChange}
              title="Moze biti negativan (npr. -1 za podrum/garazu)" /></label>
            <label>Cena zakupa (€)<input name="rentPrice" type="number" required min="1" step="1" value={form.rentPrice} onChange={handleChange} /></label>
            <label>Depozit (€)<input name="deposit" type="number" min="0" step="1" value={form.deposit} onChange={handleChange} /></label>
          </div>
          <label>Opis<textarea name="description" rows="2" value={form.description} onChange={handleChange} /></label>
          <div className="amenity-list">
            {amenities.map(p =>
              <label key={p.id} style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 0, fontWeight: 400 }}>
                <input type="checkbox" style={{ width: 'auto' }}
                  checked={form.amenityIds.includes(p.id)} onChange={() => toggleAmenity(p.id)} />
                {p.name}
              </label>
            )}
          </div>
          <div style={{ display: 'flex', gap: 10, marginTop: 10 }}>
            <button type="submit">{editId ? 'Sacuvaj izmene' : 'Dodaj nekretninu'}</button>
            {editId && <button type="button" className="btn-sek"
              onClick={() => { setForm(emptyForm); setEditId(null); setImages([]) }}>Otkazi</button>}
          </div>
        </form>
      </div>

      {/* galerija se ureduje tek nakon sto nekretnina postoji (ima id) */}
      {editId &&
        <div className="panel">
          <h2>Galerija slika</h2>
          {images.length === 0
            ? <p className="muted">Nema slika.</p>
            : <div className="gallery-admin">
                {images.map(s =>
                  <div key={s.id}>
                    <img src={s.url} alt="" />
                    {s.primary && <span className="badge-primary">Glavna</span>}
                    <button className="btn-opasno" onClick={() => removeImage(s.id)}>Obrisi</button>
                  </div>
                )}
              </div>
          }
          <form onSubmit={submitImage} className="image-form">
            <label>URL slike
              <input required type="url" placeholder="https://..." value={newImage.url}
                onChange={e => setNewImage({ ...newImage, url: e.target.value })} />
            </label>
            <label className="check-primary">
              <input type="checkbox" checked={newImage.primary}
                onChange={e => setNewImage({ ...newImage, primary: e.target.checked })} />
              Glavna
            </label>
            <button type="submit">Dodaj sliku</button>
          </form>
        </div>
      }

      <div className="panel">
        <table>
          <thead>
            <tr><th>Naziv</th><th>Grad</th><th>Tip</th><th>Cena</th><th>Status</th><th>Akcije</th></tr>
          </thead>
          <tbody>
            {properties.map(n =>
              <tr key={n.id}>
                <td>{n.name}</td>
                <td>{n.city}</td>
                <td>{n.type?.name}</td>
                <td>{n.rentPrice} €</td>
                <td>{PROPERTY_STATUS_LABEL[n.status]}</td>
                <td style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                  <button className="btn-sek" onClick={() => startEdit(n)}>Izmeni</button>
                  <button className="btn-opasno" onClick={() => remove(n.id)}>Obrisi</button>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
