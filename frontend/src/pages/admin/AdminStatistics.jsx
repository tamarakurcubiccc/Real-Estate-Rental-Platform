import { useEffect, useState } from 'react'
import {
  Chart as ChartJS,
  ArcElement, BarElement, CategoryScale, LinearScale, Tooltip, Legend
} from 'chart.js'
import { Doughnut, Bar } from 'react-chartjs-2'
import { getStatistics } from '../../api/services'
import { PROPERTY_STATUS_LABEL, BOOKING_STATUS_LABEL } from '../../constants/status.js'

ChartJS.register(ArcElement, BarElement, CategoryScale, LinearScale, Tooltip, Legend)

// brend plava + dopunske boje za grafikone
const PALETTE = ['#1f3864', '#2e5aa8', '#5b8def', '#f59e0b', '#10b981', '#ef4444', '#8b5cf6', '#14b8a6', '#e879a6', '#64748b']

const kpiBox = {
  background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10,
  padding: '18px 16px', textAlign: 'center', flex: '1 1 160px'
}

export default function AdminStatistics() {
  const [s, setS] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    getStatistics()
      .then(r => setS(r.data))
      .catch(err => setError(err.response?.data?.message || 'Greska pri ucitavanju statistike.'))
  }, [])

  if (error) return <p className="error">{error}</p>
  if (!s) return <p>Ucitavanje...</p>

  const propStatusEntries = Object.entries(s.propertiesByStatus || {})
  const bookStatusEntries = Object.entries(s.bookingsByStatus || {})

  const propertiesByStatus = {
    labels: propStatusEntries.map(([k]) => PROPERTY_STATUS_LABEL[k] || k),
    datasets: [{ data: propStatusEntries.map(([, v]) => v), backgroundColor: PALETTE }]
  }
  const bookingsByStatus = {
    labels: bookStatusEntries.map(([k]) => BOOKING_STATUS_LABEL[k] || k),
    datasets: [{ data: bookStatusEntries.map(([, v]) => v), backgroundColor: PALETTE }]
  }
  const mostRequested = {
    labels: (s.mostRequested || []).map(x => x.name),
    datasets: [{ label: 'Broj zahteva', data: (s.mostRequested || []).map(x => x.bookingCount), backgroundColor: '#2e5aa8' }]
  }
  const averageRatings = {
    labels: (s.averageRatings || []).map(x => x.name),
    datasets: [{ label: 'Prosecna ocena', data: (s.averageRatings || []).map(x => x.averageRating), backgroundColor: '#f59e0b' }]
  }

  const doughnutOpts = { maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }
  const barOpts = (max) => ({
    maintainAspectRatio: false,
    indexAxis: 'y',
    plugins: { legend: { display: false } },
    scales: { x: { beginAtZero: true, ...(max ? { max } : {}), ticks: { precision: 0, ...(max ? { stepSize: 1 } : {}) } } }
  })

  return (
    <div>
      <h1>Statistika</h1>

      {/* KPI kartice */}
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: 12, marginBottom: 20 }}>
        <div style={kpiBox}>
          <div style={{ fontSize: 28, fontWeight: 700, color: '#1f3864' }}>{s.totalProperties}</div>
          <div className="muted">Nekretnina</div>
        </div>
        <div style={kpiBox}>
          <div style={{ fontSize: 28, fontWeight: 700, color: '#1f3864' }}>{s.totalBookings}</div>
          <div className="muted">Zahteva</div>
        </div>
        <div style={kpiBox}>
          <div style={{ fontSize: 28, fontWeight: 700, color: '#1f3864' }}>{s.totalUsers}</div>
          <div className="muted">Korisnika</div>
        </div>
        <div style={kpiBox}>
          <div style={{ fontSize: 28, fontWeight: 700, color: '#1f3864' }}>{s.occupancyPercent}%</div>
          <div className="muted">Popunjenost</div>
        </div>
      </div>

      {/* Grafikoni */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: 16 }}>
        <div className="panel">
          <h2 style={{ fontSize: 16 }}>Nekretnine po statusu</h2>
          <div style={{ position: 'relative', height: 280 }}><Doughnut data={propertiesByStatus} options={doughnutOpts} /></div>
        </div>
        <div className="panel">
          <h2 style={{ fontSize: 16 }}>Zahtevi po statusu</h2>
          <div style={{ position: 'relative', height: 280 }}><Doughnut data={bookingsByStatus} options={doughnutOpts} /></div>
        </div>
        <div className="panel">
          <h2 style={{ fontSize: 16 }}>Najtrazenije nekretnine (broj zahteva)</h2>
          <div style={{ position: 'relative', height: 300 }}><Bar data={mostRequested} options={barOpts()} /></div>
        </div>
        <div className="panel">
          <h2 style={{ fontSize: 16 }}>Prosecne ocene nekretnina</h2>
          <div style={{ position: 'relative', height: 300 }}><Bar data={averageRatings} options={barOpts(5)} /></div>
        </div>
      </div>
    </div>
  )
}
