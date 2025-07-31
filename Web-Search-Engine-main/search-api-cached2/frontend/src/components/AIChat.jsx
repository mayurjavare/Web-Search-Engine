import { useState } from 'react'
import { searchAPI } from '../services/api'

const AIChat = ({ isOpen, onClose }) => {
  const [messages, setMessages] = useState([])
  const [inputMessage, setInputMessage] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const handleSendMessage = async (e) => {
    e.preventDefault()
    if (!inputMessage.trim() || isLoading) return

    const userMessage = inputMessage.trim()
    setInputMessage('')
    setIsLoading(true)

    // Add user message to chat
    const newUserMessage = {
      id: Date.now(),
      type: 'user',
      content: userMessage,
      timestamp: new Date()
    }
    setMessages(prev => [...prev, newUserMessage])

    try {
      // Call AI API
      const aiResponse = await searchAPI.askAI(userMessage)
      
      // Add AI response to chat
      const newAIMessage = {
        id: Date.now() + 1,
        type: 'ai',
        content: aiResponse,
        timestamp: new Date()
      }
      setMessages(prev => [...prev, newAIMessage])
    } catch (error) {
      console.error('Error getting AI response:', error)
      // Add error message
      const errorMessage = {
        id: Date.now() + 1,
        type: 'error',
        content: 'Sorry, I encountered an error. Please try again.',
        timestamp: new Date()
      }
      setMessages(prev => [...prev, errorMessage])
    } finally {
      setIsLoading(false)
    }
  }

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      handleSendMessage(e)
    }
  }

  if (!isOpen) return null

  return (
    <div className="ai-chat-overlay">
      <div className="ai-chat-modal">
        <div className="ai-chat-header">
          <h3>🤖 AI Assistant</h3>
          <button className="ai-chat-close" onClick={onClose}>
            ✕
          </button>
        </div>
        
        <div className="ai-chat-messages">
          {messages.length === 0 && (
            <div className="ai-chat-welcome">
              <p>👋 Hello! I'm your AI assistant. Ask me anything!</p>
            </div>
          )}
          
          {messages.map((message) => (
            <div key={message.id} className={`ai-message ${message.type}`}>
              <div className="ai-message-content">
                {message.content}
              </div>
              <div className="ai-message-time">
                {message.timestamp.toLocaleTimeString()}
              </div>
            </div>
          ))}
          
          {isLoading && (
            <div className="ai-message ai">
              <div className="ai-message-content">
                <div className="ai-typing">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          )}
        </div>
        
        <form className="ai-chat-input" onSubmit={handleSendMessage}>
          <input
            type="text"
            value={inputMessage}
            onChange={(e) => setInputMessage(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Type your message..."
            disabled={isLoading}
          />
          <button type="submit" disabled={isLoading || !inputMessage.trim()}>
            {isLoading ? '⏳' : '➤'}
          </button>
        </form>
      </div>
    </div>
  )
}

export default AIChat 