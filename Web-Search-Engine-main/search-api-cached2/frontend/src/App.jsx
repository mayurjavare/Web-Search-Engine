import { useState, useRef } from 'react'
import { searchAPI } from './services/api'
import Header from './components/Header'
import SearchBar from './components/SearchBar'
import LoadingSpinner from './components/LoadingSpinner'
import ResultsTabs from './components/ResultsTabs'
import InstantAnswer from './components/InstantAnswer'
import SearchResults from './components/SearchResults'
import ImageResults from './components/ImageResults'
import AIResponse from './components/AIResponse'
import './App.css'

function App() {
  const [searchResults, setSearchResults] = useState([])
  const [instantAnswer, setInstantAnswer] = useState(null)
  const [imageResults, setImageResults] = useState([])
  const [loading, setLoading] = useState(false)
  const [activeTab, setActiveTab] = useState('results')
  const [currentPage, setCurrentPage] = useState(0)
  const [hasMoreResults, setHasMoreResults] = useState(false)
  const [currentQuery, setCurrentQuery] = useState('')
  const [isAIResponseOpen, setIsAIResponseOpen] = useState(false)
  const isNavigating = useRef(false)

  const handleSearch = async (query, page = 0) => {
    setLoading(true)
    setCurrentQuery(query)
    setCurrentPage(page)

    try {
      // Fetch search results with pagination
      const searchData = await searchAPI.search(query, page, 10)
      setSearchResults(searchData)
      
      // Simple logic: if we get 10 results, assume there might be more
      setHasMoreResults(searchData.length === 10)

      // Fetch instant answer and images only on first page
      if (page === 0) {
        const instantData = await searchAPI.getInstantAnswer(query)
        setInstantAnswer(instantData)
        
        const imageData = await searchAPI.searchImages(query)
        setImageResults(imageData)
      }

    } catch (error) {
      console.error('Error performing search:', error)
    } finally {
      setLoading(false)
    }
  }

  const handlePageChange = async (newPage) => {
    console.log('handlePageChange called with:', newPage, 'Current page:', currentPage, 'isNavigating:', isNavigating.current, 'loading:', loading)
    
    // Prevent rapid clicks and ensure we're not already navigating
    if (isNavigating.current || loading) {
      console.log('Navigation blocked - already navigating or loading')
      return
    }
    
    if (newPage >= 0 && newPage !== currentPage) {
      isNavigating.current = true
      console.log('Starting navigation to page:', newPage)
      
      try {
        await handleSearch(currentQuery, newPage)
        console.log('Navigation completed to page:', newPage)
      } catch (error) {
        console.error('Navigation error:', error)
      } finally {
        // Ensure flag is reset after a short delay to prevent rapid clicks
        setTimeout(() => {
          isNavigating.current = false
          console.log('Navigation flag reset')
        }, 100)
      }
    } else {
      console.log('Navigation skipped - same page or invalid page')
    }
  }

  const hasResults = searchResults.length > 0 || instantAnswer || imageResults.length > 0

  const handleAskAI = () => {
    setIsAIResponseOpen(true)
  }

  const handleCloseAIResponse = () => {
    setIsAIResponseOpen(false)
  }

  return (
    <div className="app">
      <Header onAskAI={handleAskAI} />
      
      <main className="main">
        <div className="container">
          <SearchBar onSearch={handleSearch} loading={loading} />

          {loading && <LoadingSpinner />}

          {!loading && hasResults && (
            <div className="results-section">
              <ResultsTabs activeTab={activeTab} onTabChange={setActiveTab} />

              <div className="results-content">
                {activeTab === 'results' && (
                  <>
                    {currentPage === 0 && <InstantAnswer instantAnswer={instantAnswer} />}
                    <SearchResults 
                      searchResults={searchResults} 
                      currentPage={currentPage}
                      onPageChange={handlePageChange}
                      hasMoreResults={hasMoreResults}
                    />
                  </>
                )}

                {activeTab === 'images' && (
                  <ImageResults imageResults={imageResults} />
                )}
              </div>
            </div>
          )}
        </div>
      </main>
      
      <AIResponse isVisible={isAIResponseOpen} onClose={handleCloseAIResponse} />
    </div>
  )
}

export default App
