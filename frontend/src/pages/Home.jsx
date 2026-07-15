import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { getProperties, getPropertyTypes } from '../api/services'
import PropertyCard from '../components/PropertyCard.jsx'

const emptyPage = { content: [], totalPages: 0, totalElements: 0, number: 0 }
const FILTER_KEYS = ['city', 'typeId', 'minPrice', 'maxPrice', 'rooms']

export default function Home() {
  // URL cuva broj strane i filtere (deljiv link, ocuvano stanje pretrage)
  const [searchParams, setSearchParams] = useSearchParams()
  const [page, setPage] = useState(emptyPage)
  const [types, setTypes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const pageNumber = Math.max(parseInt(searchParams.get('page'), 10) || 0, 0)

  // kontrolisana polja forme, inicijalizovana iz URL-a
  const [filters, setFilters] = useState(() => {
    const f = { city: '', typeId: '', minPrice: '', maxPrice: '', rooms: '' }
    FILTER_KEYS.forEach(k => { const v = searchParams.get(k); if (v) f[k] = v })
    return f
  })

  useEffect(() => { getPropertyTypes().then(r => setTypes(r.data)) }, [])

  // ucitavanje prati URL: svaka promena strane ili filtera pokrece novo ucitavanje
  useEffect(() => {
    const load = async () => {
      setLoading(true)
      setError('')
      // saljemo samo popunjene filtere iz URL-a
      const params = { page: pageNumber }
      FILTER_KEYS.forEach(k => { const v = searchParams.get(k); if (v) params[k] = v })
      try {
        const res = await getProperties(params)
        setPage(res.data)
      } catch (err) {
        setError(err.response?.data?.message || 'Greska pri ucitavanju nekretnina.')
        setPage(emptyPage)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [searchParams])

  const handleChange = (e) => setFilters({ ...filters, [e.target.name]: e.target.value })

  // nova pretraga: upisi filtere u URL i vrati se na prvu stranu
  const search = () => {
    const next = new URLSearchParams()
    FILTER_KEYS.forEach(k => { if (filters[k] !== '') next.set(k, filters[k]) })
    setSearchParams(next) // bez page parametra -> strana 0
  }

  // promena strane cuva postojece filtere i menja samo broj strane
  const goToPage = (i) => {
    const next = new URLSearchParams(searchParams)
    next.set('page', String(i))
    setSearchParams(next)
  }

  const properties = page.content || []

  return (
    <div>
      <h1>Ponuda nekretnina</h1>

      <div className="panel">
        <div className="filters">
          <label>Grad
            <input name="city" value={filters.city} onChange={handleChange} placeholder="npr. Beograd" />
          </label>
          <label>Tip
            <select name="typeId" value={filters.typeId} onChange={handleChange}>
              <option value="">Svi tipovi</option>
              {types.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
            </select>
          </label>
          <label>Cena od (€)
            <input name="minPrice" type="number" min="0" step="1" value={filters.minPrice} onChange={handleChange} />
          </label>
          <label>Cena do (€)
            <input name="maxPrice" type="number" min="0" step="1" value={filters.maxPrice} onChange={handleChange} />
          </label>
          <label>Min. soba
            <input name="rooms" type="number" min="0" step="1" value={filters.rooms} onChange={handleChange} />
          </label>
          <button onClick={search}>Pretrazi</button>
        </div>
      </div>

      {loading
        ? <p>Ucitavanje...</p>
        : error
          ? <p className="error">{error}</p>
        : properties.length === 0
          ? <p>Nema nekretnina za zadate kriterijume.</p>
          : <>
              <div className="grid">
                {properties.map(n => <PropertyCard key={n.id} n={n} />)}
              </div>

              {page.totalPages > 1 &&
                <>
                  <nav className="pagination" aria-label="Paginacija">
                    <button className="btn-sek" disabled={page.first}
                      onClick={() => goToPage(pageNumber - 1)} aria-label="Prethodna">&laquo;</button>

                    {Array.from({ length: page.totalPages }, (_, i) =>
                      <button key={i}
                        className={i === page.number ? 'active' : 'btn-sek'}
                        onClick={() => goToPage(i)}>{i + 1}</button>
                    )}

                    <button className="btn-sek" disabled={page.last}
                      onClick={() => goToPage(pageNumber + 1)} aria-label="Sledeca">&raquo;</button>
                  </nav>
                  <p className="pagination-info">
                    Strana {page.number + 1} od {page.totalPages} · ukupno {page.totalElements} nekretnina
                  </p>
                </>
              }
            </>
      }
    </div>
  )
}
