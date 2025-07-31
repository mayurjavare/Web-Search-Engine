const API_BASE_URL = 'http://localhost:8081/api'

export const searchAPI = {
  // Search for web results
  search: async (query, page = 0, pageSize = 10) => {
    try {
      const response = await fetch(`${API_BASE_URL}/search?query=${encodeURIComponent(query)}&page=${page}&pageSize=${pageSize}`)
      if (response.ok) {
        return await response.json()
      }
      return []
    } catch (error) {
      console.error('Error fetching search results:', error)
      return []
    }
  },

  // Get autocomplete suggestions
  getSuggestions: async (query) => {
    try {
      const response = await fetch(`${API_BASE_URL}/autoComplete?query=${encodeURIComponent(query)}`)
      if (response.ok) {
        const data = await response.json()
        // Handle the response format: array of objects with 'phrase' property
        if (Array.isArray(data)) {
          return data
        } else if (typeof data === 'string') {
          // Fallback for string responses
          return [data]
        } else {
          return []
        }
      }
      return []
    } catch (error) {
      console.error('Error fetching suggestions:', error)
      return []
    }
  },

  // Get instant answer
  getInstantAnswer: async (query) => {
    try {
      const response = await fetch(`${API_BASE_URL}/search/ddg-instant?query=${encodeURIComponent(query)}`)
      if (response.ok) {
        return await response.json()
      }
      return null
    } catch (error) {
      console.error('Error fetching instant answer:', error)
      return null
    }
  },

  // Search for images
  searchImages: async (query) => {
    try {
      const response = await fetch(`${API_BASE_URL}/search/images?query=${encodeURIComponent(query)}`)
      if (response.ok) {
        return await response.json()
      }
      return []
    } catch (error) {
      console.error('Error fetching image results:', error)
      return []
    }
  }
} 