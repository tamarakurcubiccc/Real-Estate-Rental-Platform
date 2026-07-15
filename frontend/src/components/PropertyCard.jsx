import { Link } from 'react-router-dom'
import { PROPERTY_STATUS_CLASS, PROPERTY_STATUS_LABEL } from '../constants/status.js'

export default function PropertyCard({ n }) {
  const primary = n.images?.find(s => s.primary) || n.images?.[0]
  return (
    <Link to={`/property/${n.id}`} className="card">
      <img src={primary ? primary.url : 'https://picsum.photos/seed/nekr/800/500'} alt={n.name} />
      <div className="card-body">
        <h3>{n.name}
          <span className={`badge ${PROPERTY_STATUS_CLASS[n.status]}`}>{PROPERTY_STATUS_LABEL[n.status]}</span>
        </h3>
        <div className="muted">{n.city} · {n.type?.name} · {n.area} m²</div>
        {n.reviewCount > 0 &&
          <div className="stars" style={{ fontSize: 13, marginTop: 4 }}>
            ★ {n.averageRating} ({n.reviewCount})
          </div>}
        <div className="price">{n.rentPrice} € / mes</div>
      </div>
    </Link>
  )
}
