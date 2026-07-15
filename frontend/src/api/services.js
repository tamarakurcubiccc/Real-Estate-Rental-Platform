import client from './client'

// --- Auth ---
export const register = (data) => client.post('/auth/register', data)
export const login = (data) => client.post('/auth/login', data)

// --- Properties ---
export const getProperties = (params) => client.get('/properties', { params })
export const getProperty = (id) => client.get(`/properties/${id}`)
export const createProperty = (data) => client.post('/properties', data)
export const updateProperty = (id, data) => client.put(`/properties/${id}`, data)
export const deleteProperty = (id) => client.delete(`/properties/${id}`)
export const addImage = (id, data) => client.post(`/properties/${id}/images`, data)
export const deleteImage = (id, imageId) => client.delete(`/properties/${id}/images/${imageId}`)

// --- Property types and amenities ---
export const getPropertyTypes = () => client.get('/property-types')
export const getAmenities = () => client.get('/amenities')

// --- Seasonal prices ---
export const getSeasonalPrices = (propertyId) => client.get(`/properties/${propertyId}/seasonal-prices`)
export const addSeason = (propertyId, data) => client.post(`/properties/${propertyId}/seasonal-prices`, data)
export const updateSeason = (propertyId, seasonalPriceId, data) =>
  client.put(`/properties/${propertyId}/seasonal-prices/${seasonalPriceId}`, data)
export const deleteSeason = (propertyId, seasonalPriceId) =>
  client.delete(`/properties/${propertyId}/seasonal-prices/${seasonalPriceId}`)

// --- Bookings ---
export const createBooking = (data) => client.post('/bookings', data)
export const getBookings = () => client.get('/bookings')
export const getMyBookings = (userId) => client.get(`/bookings/user/${userId}`)
export const changeBookingStatus = (id, status) => client.put(`/bookings/${id}/status`, null, { params: { status } })
export const cancelBooking = (id, userId) => client.put(`/bookings/${id}/cancel`, null, { params: { userId } })

// --- Statistics (admin) ---
export const getStatistics = () => client.get('/statistics')

// --- Reviews ---
export const getReviews = (propertyId) => client.get(`/reviews/property/${propertyId}`)
export const getPendingReviews = () => client.get('/reviews/pending')
export const createReview = (data) => client.post('/reviews', data)
export const approveReview = (id) => client.put(`/reviews/${id}/approve`)
export const deleteReview = (id) => client.delete(`/reviews/${id}`)
