import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { register } from '../api/services'
import { useAuth } from '../context/AuthContext.jsx'

export default function Register() {
  const [credentials, setCredentials] = useState({ firstName: '', lastName: '', email: '', password: '', phone: '' })
  const [error, setError] = useState('')
  const { signIn } = useAuth()
  const navigate = useNavigate()

  const handleChange = (e) => setCredentials({ ...credentials, [e.target.name]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const res = await register(credentials)
      signIn(res.data.user, res.data.token)
      navigate('/')
    } catch (err) {
      const data = err.response?.data
      // prikaz greske validacije ili poslovne greske
      if (data?.validationErrors) setError(Object.values(data.validationErrors).join(' '))
      else setError(data?.message || 'Neuspesna registracija.')
    }
  }

  return (
    <div style={{ maxWidth: 480, margin: '0 auto' }}>
      <div className="panel">
        <h1>Registracija</h1>
        {error && <p className="error">{error}</p>}
        <form onSubmit={submit}>
          <label>Ime<input name="firstName" required value={credentials.firstName} onChange={handleChange} /></label>
          <label>Prezime<input name="lastName" required value={credentials.lastName} onChange={handleChange} /></label>
          <label>Email<input name="email" type="email" required value={credentials.email} onChange={handleChange} /></label>
          <label>Lozinka<input name="password" type="password" required value={credentials.password} onChange={handleChange} /></label>
          <label>Telefon<input name="phone" value={credentials.phone} onChange={handleChange} /></label>
          <button type="submit">Registruj se</button>
        </form>
        <p style={{ marginTop: 16, fontSize: 14 }}>
          Vec imate nalog? <Link to="/login">Prijavite se</Link>
        </p>
      </div>
    </div>
  )
}
