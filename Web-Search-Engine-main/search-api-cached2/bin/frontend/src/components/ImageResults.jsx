import { useState } from 'react'

const ImageResults = ({ imageResults }) => {
  if (!imageResults || imageResults.length === 0) return null

  const handleImageClick = (image) => {
    console.log('Image clicked:', image)
    console.log('Image source URL:', image.source)
    
    // Redirect to the source URL (the one displayed below the image)
    let targetUrl = image.source
    
    // Validate URL format
    if (!targetUrl || (!targetUrl.startsWith('http://') && !targetUrl.startsWith('https://'))) {
      console.log('Invalid URL format for image click:', targetUrl)
      return
    }
    
    console.log('Image redirecting to source:', targetUrl)
    window.open(targetUrl, '_blank', 'noopener,noreferrer')
  }

  const handleImageLinkClick = (e, url) => {
    e.preventDefault() // Prevent default link behavior
    e.stopPropagation() // Prevent image click
    
    console.log('Link clicked with URL:', url)
    
    // Validate URL format
    if (!url || (!url.startsWith('http://') && !url.startsWith('https://'))) {
      console.log('Invalid URL format for link:', url)
      return
    }
    
    console.log('Link redirecting to:', url)
    window.open(url, '_blank', 'noopener,noreferrer')
  }

  return (
    <div className="image-results">
      <h3>Image Results</h3>
      <div className="image-grid">
        {imageResults.map((image, index) => {
          console.log(`Image ${index}:`, image)
          return (
            <div key={index} className="image-item">
              <div className="image-container" onClick={() => handleImageClick(image)}>
                <img src={image.imgUrl} alt={image.title} />
              </div>
              <div className="image-info">
                <p className="image-title">{image.title}</p>
                <button 
                  className="image-url-link"
                  onClick={(e) => handleImageLinkClick(e, image.source)}
                >
                  {image.source}
                </button>
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}

export default ImageResults 