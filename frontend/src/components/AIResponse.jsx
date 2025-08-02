import { useState } from 'react'
import { searchAPI } from '../services/api'

const AIResponse = ({ isVisible, onClose }) => {
  const [prompt, setPrompt] = useState('')
  const [response, setResponse] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!prompt.trim() || isLoading) return

    setIsLoading(true)
    setResponse('')
    setError('')

    try {
      const aiResponse = await searchAPI.askAI(prompt.trim())
      if (aiResponse && aiResponse.trim()) {
        setResponse(aiResponse)
      } else {
        setError('No response received from AI. Please try again.')
      }
    } catch (error) {
      console.error('Error getting AI response:', error)
      setError('Unable to connect to AI service. Please check your internet connection and try again.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleClose = () => {
    setPrompt('')
    setResponse('')
    setError('')
    onClose()
  }

  if (!isVisible) return null

  return (
    <div className="ai-response-overlay">
      <div className="ai-response-modal">
        <div className="ai-response-header">
          <h3>AI Assistant</h3>
          <button className="ai-response-close" onClick={handleClose}>
            ✕
          </button>
        </div>
        
        <div className="ai-response-content">
          <form onSubmit={handleSubmit} className="ai-prompt-form">
            <input
              type="text"
              value={prompt}
              onChange={(e) => setPrompt(e.target.value)}
              placeholder="Ask me anything..."
              disabled={isLoading}
              className="ai-prompt-input"
            />
            <button type="submit" disabled={isLoading || !prompt.trim()}>
              {isLoading ? 'Processing...' : 'Ask AI'}
            </button>
          </form>
          
          {error && (
            <div className="ai-error-text">
              <h4>Error:</h4>
              <div className="error-content">
                {error}
              </div>
            </div>
          )}
          
          {response && (
            <div className="ai-response-text">
              <h4>Response:</h4>
              <div className="response-content">
                {response.split('\n').map((line, index) => (
                  <p key={index}>{line}</p>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default AIResponse 