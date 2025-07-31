const SearchResults = ({ searchResults, currentPage, onPageChange, hasMoreResults }) => {
  if (!searchResults || searchResults.length === 0) return null

  const handlePageChange = (newPage) => {
    console.log('Page change requested:', newPage, 'Current page:', currentPage)
    onPageChange(newPage)
  }

  // Function to strip HTML tags from text
  const stripHtmlTags = (html) => {
    if (!html) return ''
    return html.replace(/<[^>]*>/g, '')
  }

  return (
    <div className="search-results">
      <h3>Search Results</h3>
      
      {/* Search Results */}
      <div className="results-list">
        {searchResults.map((result, index) => (
          <div key={index} className="result-item">
            <h4 className="result-title">
              <a href={result.url} target="_blank" rel="noopener noreferrer">
                {stripHtmlTags(result.title)}
              </a>
            </h4>
            <p className="result-url">{result.url}</p>
            <p className="result-snippet">{stripHtmlTags(result.description)}</p>
          </div>
        ))}
      </div>
      
      {/* Pagination - show if we have results */}
      {searchResults.length > 0 && (
        <div className="pagination">
          <button 
            className="pagination-btn"
            onClick={(e) => {
              e.preventDefault()
              e.stopPropagation()
              if (currentPage > 0) {
                handlePageChange(currentPage - 1)
              }
            }}
            disabled={currentPage <= 0}
          >
            Previous
          </button>
          <span className="page-info">
            Page {currentPage + 1}
          </span>
          <button 
            className="pagination-btn"
            onClick={(e) => {
              e.preventDefault()
              e.stopPropagation()
              if (hasMoreResults) {
                handlePageChange(currentPage + 1)
              }
            }}
            disabled={!hasMoreResults}
          >
            Next
          </button>
        </div>
      )}
    </div>
  )
}

export default SearchResults 