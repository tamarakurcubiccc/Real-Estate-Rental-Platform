import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { login } from '../api/services'
import { useAuth } from '../context/AuthContext.jsx'

export default function Login() {
  const [credentials, setCredentials] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const { signIn } = useAuth()
  const navigate = useNavigate()

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const res = await login(credentials)
      signIn(res.data.user, res.data.token)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.message || 'Neuspesna prijava.')
    }
  }

  return (
    <div style={{ maxWidth: 420, margin: '0 auto' }}>
      <div className="panel">
        <h1>Prijava</h1>
        {error && <p className="error">{error}</p>}
        <form onSubmit={submit}>
          <label>Email
            <input type="email" required value={credentials.email}
              onChange={e => setCredentials({ ...credentials, email: e.target.value })} />
          </label>
          <label>Lozinka
            <input type="password" required value={credentials.password}
              onChange={e => setCredentials({ ...credentials, password: e.target.value })} />
          </label>
          <button type="submit">Prijavi se</button>
        </form>
        <p style={{ marginTop: 16, fontSize: 14 }}>
          Nemate nalog? <Link to="/register">Registrujte se</Link>
        </p>
        <p style={{ fontSize: 12, color: '#64748b' }}>
          Test admin: admin@nekretnine.rs / admin123<br />
          Test korisnik: pera@example.com / pera123
        </p>
      </div>
    </div>
  )
}
