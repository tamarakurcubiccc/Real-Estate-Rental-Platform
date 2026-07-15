import { Routes, Route, Navigate } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import Home from './pages/Home.jsx'
import PropertyDetails from './pages/PropertyDetails.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import MyBookings from './pages/MyBookings.jsx'
import AdminProperties from './pages/admin/AdminProperties.jsx'
import AdminBookings from './pages/admin/AdminBookings.jsx'
import AdminReviews from './pages/admin/AdminReviews.jsx'
import AdminSeasonalPrices from './pages/admin/AdminSeasonalPrices.jsx'
import AdminStatistics from './pages/admin/AdminStatistics.jsx'
import { useAuth } from './context/AuthContext.jsx'

// jednostavna zastita ruta
function ProtectedRoute({ children, adminOnly }) {
  const { user, isAdmin } = useAuth()
  if (!user) return <Navigate to="/login" replace />
  if (adminOnly && !isAdmin) return <Navigate to="/" replace />
  return children
}

export default function App() {
  return (
    <>
      <Navbar />
      <main className="container" style={{ padding: '24px 16px' }}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/property/:id" element={<PropertyDetails />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/my-bookings" element={<ProtectedRoute><MyBookings /></ProtectedRoute>} />
          <Route path="/admin/properties" element={<ProtectedRoute adminOnly><AdminProperties /></ProtectedRoute>} />
          <Route path="/admin/bookings" element={<ProtectedRoute adminOnly><AdminBookings /></ProtectedRoute>} />
          <Route path="/admin/reviews" element={<ProtectedRoute adminOnly><AdminReviews /></ProtectedRoute>} />
          <Route path="/admin/seasonal-prices" element={<ProtectedRoute adminOnly><AdminSeasonalPrices /></ProtectedRoute>} />
          <Route path="/admin/statistics" element={<ProtectedRoute adminOnly><AdminStatistics /></ProtectedRoute>} />
        </Routes>
      </main>
    </>
  )
}
