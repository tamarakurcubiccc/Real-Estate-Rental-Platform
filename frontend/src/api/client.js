import axios from 'axios'

// Bazni URL: u Docker-u se preko VITE_API_URL postavlja na "/api" (nginx proksi),
// a lokalno pada na direktan poziv Spring backenda.
const baseURL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'

const client = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' }
})

// Request interceptor: dodaje "Authorization: Bearer <token>" ako token postoji
client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor: na 401 (istekao/nevalidan token) cisti sesiju
client.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      // izbegni beskonacnu petlju ako smo vec na prijavi
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login'
      }
    }
    return Promise.reject(err)
  }
)

export default client
