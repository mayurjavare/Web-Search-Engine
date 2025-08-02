const ResultsTabs = ({ activeTab, onTabChange }) => {
  return (
    <div className="tabs">
      <button 
        className={`tab ${activeTab === 'results' ? 'active' : ''}`}
        onClick={() => onTabChange('results')}
      >
        Search Results
      </button>
      <button 
        className={`tab ${activeTab === 'images' ? 'active' : ''}`}
        onClick={() => onTabChange('images')}
      >
        Images
      </button>
    </div>
  )
}

export default ResultsTabs 