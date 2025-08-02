import React from 'react'

const ImageResults = ({ imageResults }) => {
  const handleImageClick = (image) => {
    console.log('=== IMAGE CLICK DEBUG ===')
    console.log('Image clicked:', image)
    console.log('Image source URL:', image.source)
    console.log('Image title:', image.title)
    console.log('Image imgUrl:', image.imgUrl)
    
    // Validate URL before redirecting
    if (image.source && (image.source.startsWith('http://') || image.source.startsWith('https://'))) {
      console.log('✅ Valid URL - Redirecting to:', image.source)
      window.open(image.source, '_blank', 'noopener,noreferrer')
    } else {
      console.error('❌ Invalid URL:', image.source)
      alert('Invalid source URL: ' + image.source)
    }
  }

  const handleImageLinkClick = (e, url) => {
    e.preventDefault()
    e.stopPropagation()
    console.log('Image link clicked:', url)
    
    // Validate URL before redirecting
    if (url && (url.startsWith('http://') || url.startsWith('https://'))) {
      console.log('Redirecting to:', url)
      window.open(url, '_blank', 'noopener,noreferrer')
    } else {
      console.error('Invalid URL:', url)
    }
  }

  if (!imageResults || imageResults.length === 0) {
    return (
      <div className="image-results">
        <h3>No images found</h3>
      </div>
    )
  }

  return (
    <div className="image-results">
      <h3>Image Results ({imageResults.length} images)</h3>
      <div className="image-grid">
        {imageResults.map((image, index) => (
          <div key={index} className="image-card">
            <div 
              className="image-container" 
              onClick={() => handleImageClick(image)}
              style={{ cursor: 'pointer' }}
              title={`Click to visit: ${image.source || 'No source available'}`}
            >
              <img 
                src={image.imgUrl} 
                alt={image.title || 'Image'} 
                loading="lazy"
                onError={(e) => {
                  e.target.style.display = 'none'
                  e.target.nextSibling.style.display = 'block'
                }}
              />
              <div className="image-fallback" style={{ display: 'none' }}>
                <span>Image not available</span>
              </div>
            </div>
            
            <div className="image-details">
              <h4 className="image-title" title={image.title}>
                {image.title || 'Untitled'}
              </h4>
              <div className="image-source">
                <span className="source-label">Source:</span>
                <button 
                  className="source-link"
                  onClick={(e) => handleImageLinkClick(e, image.source)}
                  title={image.source}
                >
                  {image.source ? 
                    (image.source.length > 50 ? 
                      image.source.substring(0, 50) + '...' : 
                      image.source
                    ) : 
                    'No source available'
                  }
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default ImageResults 