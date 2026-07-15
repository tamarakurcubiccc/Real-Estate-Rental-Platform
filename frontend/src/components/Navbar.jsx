import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function Navbar() {
  const { user, isAdmin, signOut } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => { signOut(); navigate('/') }

  return (
    <nav className="navbar">
      <div className="container">
        <Link to="/" className="brand">Nekretnine</Link>
        <Link to="/">Ponuda</Link>
        {user && !isAdmin && <Link to="/my-bookings">Moji zahtevi</Link>}
        {isAdmin && <Link to="/admin/properties">Nekretnine</Link>}
        {isAdmin && <Link to="/admin/bookings">Zahtevi</Link>}
        {isAdmin && <Link to="/admin/reviews">Recenzije</Link>}
        {isAdmin && <Link to="/admin/seasonal-prices">Cenovnik</Link>}
        {isAdmin && <Link to="/admin/statistics">Statistika</Link>}
        <div className="desno">
          {user
            ? <>
                <span style={{ color: '#dbe4ff', fontSize: 14 }}>{user.firstName} {user.lastName}</span>
                <button className="btn-sek" onClick={handleLogout}>Odjava</button>
              </>
            : <>
                <Link to="/login">Prijava</Link>
                <Link to="/register">Registracija</Link>
              </>
          }
        </div>
      </div>
    </nav>
  )
}
