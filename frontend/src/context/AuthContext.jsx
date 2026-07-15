import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

// Ucitavanje pocetnog stanja iz localStorage (opstanak prijave nakon refresh-a)
function loadUser() {
  try {
    const raw = localStorage.getItem('user')
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(loadUser)

  // prijava cuva i korisnika i JWT token
  const signIn = (k, token) => {
    setUser(k)
    localStorage.setItem('user', JSON.stringify(k))
    if (token) localStorage.setItem('token', token)
  }

  const signOut = () => {
    setUser(null)
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  const isAdmin = user?.role === 'ADMIN'

  return (
    <AuthContext.Provider value={{ user, signIn, signOut, isAdmin }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
