import { useState, useEffect, useRef } from 'react'
import { searchAPI } from '../services/api'

const SearchBar = ({ onSearch, loading }) => {
  const [query, setQuery] = useState('')
  const [suggestions, setSuggestions] = useState([])
  const [showSuggestions, setShowSuggestions] = useState(false)
  const suggestionsRef = useRef(null)

  // Fetch suggestions immediately when query changes (starting from first letter)
  useEffect(() => {
    if (query.trim().length > 0) {
      fetchSuggestions(query)
      setShowSuggestions(true)
    } else {
      setSuggestions([])
      setShowSuggestions(false)
    }
  }, [query])

  const fetchSuggestions = async (searchQuery) => {
    try {
      const suggestions = await searchAPI.getSuggestions(searchQuery)
      console.log('Fetched suggestions:', suggestions)
      setSuggestions(suggestions)
    } catch (error) {
      console.error('Error fetching suggestions:', error)
    }
  }

  const handleSuggestionClick = (suggestion) => {
    console.log('Suggestion clicked:', suggestion)
    
    // Handle both string and object formats
    let suggestionText = ''
    if (typeof suggestion === 'string') {
      suggestionText = suggestion
    } else if (suggestion && suggestion.phrase) {
      suggestionText = suggestion.phrase
    } else {
      console.error('Invalid suggestion format:', suggestion)
      return
    }
    
    console.log('Extracted suggestion text:', suggestionText)
    
    // Update the input with the selected suggestion
    setQuery(suggestionText)
    setShowSuggestions(false)
    
    console.log('Query updated to:', suggestionText)
  }

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch()
    }
  }

  const handleSearch = () => {
    if (!query.trim()) return
    setShowSuggestions(false)
    onSearch(query)
  }

  const handleInputChange = (e) => {
    const newValue = e.target.value
    console.log('Input changed to:', newValue)
    setQuery(newValue)
  }

  const handleInputFocus = () => {
    console.log('Input focused, query:', query, 'suggestions:', suggestions.length)
    if (query.trim().length > 0 && suggestions.length > 0) {
      setShowSuggestions(true)
    }
  }

  const handleInputBlur = (e) => {
    // Check if the click was inside the suggestions container
    if (suggestionsRef.current && suggestionsRef.current.contains(e.relatedTarget)) {
      return // Don't hide suggestions if clicking inside them
    }
    
    // Only hide suggestions if clicking outside
    setTimeout(() => {
      setShowSuggestions(false)
    }, 100)
  }

  console.log('Render state:', { query, suggestions: suggestions.length, showSuggestions })

  return (
    <div className="search-section">
      <div className="search-container">
        <div className="search-input-wrapper">
          <input
            type="text"
            value={query}
            onChange={handleInputChange}
            onKeyPress={handleKeyPress}
            onFocus={handleInputFocus}
            onBlur={handleInputBlur}
            placeholder="Search for anything..."
            className="search-input"
          />
          <button 
            onClick={handleSearch}
            disabled={loading}
            className="search-button"
          >
            {loading ? '🔍' : 'Search'}
          </button>
        </div>
        
        {showSuggestions && suggestions.length > 0 && (
          <div className="suggestions" ref={suggestionsRef}>
            <div style={{ padding: '0.5rem', fontSize: '0.8rem', color: '#666', borderBottom: '1px solid #eee' }}>
              {suggestions.length} suggestions found
            </div>
            {suggestions.map((suggestion, index) => {
              // Handle both string and object formats
              const suggestionText = typeof suggestion === 'string' ? suggestion : suggestion.phrase
              return (
                <button
                  key={index}
                  className="suggestion-item"
                  type="button"
                  onMouseDown={(e) => {
                    e.preventDefault() // Prevent input blur
                    handleSuggestionClick(suggestion)
                  }}
                  style={{ 
                    width: '100%', 
                    textAlign: 'left',
                    border: 'none',
                    background: 'none',
                    cursor: 'pointer',
                    padding: '0.75rem 1rem',
                    fontSize: '0.9rem'
                  }}
                >
                  {suggestionText}
                </button>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}

export default SearchBar 