const InstantAnswer = ({ instantAnswer }) => {
  if (!instantAnswer) return null

  // Check if there's any content to display
  const hasAbstractText = instantAnswer.AbstractText && instantAnswer.AbstractText.trim() !== ''
  const hasAnswer = instantAnswer.Answer && instantAnswer.Answer.trim() !== ''
  const hasRelatedTopics = instantAnswer.RelatedTopics && instantAnswer.RelatedTopics.length > 0

  // If no content is available, don't render anything
  if (!hasAbstractText && !hasAnswer && !hasRelatedTopics) {
    return null
  }

  return (
    <div className="instant-answer">
      {hasAbstractText && (
        <p className="abstract">{instantAnswer.AbstractText}</p>
      )}
      {hasAnswer && (
        <p className="answer">{instantAnswer.Answer}</p>
      )}
      {hasRelatedTopics && (
        <div className="related-topics">
          <ul>
            {instantAnswer.RelatedTopics.map((topic, index) => (
              <li key={index}>
                <a href={topic.FirstURL} target="_blank" rel="noopener noreferrer">
                  {topic.Text}
                </a>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  )
}

export default InstantAnswer 