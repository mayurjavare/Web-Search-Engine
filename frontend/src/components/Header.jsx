const Header = ({ onAskAI }) => {
  return (
    <header className="header">
      <div className="container">
        <div className="header-content">
          <h1 className="logo">🔍 Search Engine</h1>
          <button 
            className="ask-ai-btn"
            onClick={onAskAI}
            title="Ask AI Assistant"
          >
            Ask AI
          </button>
        </div>
      </div>
    </header>
  )
}

export default Header 