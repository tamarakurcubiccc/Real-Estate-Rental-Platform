import { useEffect } from 'react'

/**
 * Lightbox gallery: prikazuje uvecanu sliku preko celog ekrana,
 * sa listanjem strelicama, tastaturom (<- ->) i zatvaranjem na Esc.
 */
export default function Lightbox({ images, index, onClose, onChange }) {
  const total = images.length

  useEffect(() => {
    const onKey = (e) => {
      if (e.key === 'Escape') onClose()
      if (e.key === 'ArrowRight') onChange((index + 1) % total)
      if (e.key === 'ArrowLeft') onChange((index - 1 + total) % total)
    }
    document.addEventListener('keydown', onKey)
    // sprecava skrolovanje pozadine dok je lightbox otvoren
    document.body.style.overflow = 'hidden'
    return () => {
      document.removeEventListener('keydown', onKey)
      document.body.style.overflow = ''
    }
  }, [index, total, onClose, onChange])

  return (
    // klik na pozadinu zatvara
    <div className="lightbox" onClick={onClose}>
      <div className="lightbox-content" onClick={e => e.stopPropagation()}>
        <button className="lightbox-close" onClick={onClose} aria-label="Zatvori">&times;</button>

        <img className="lightbox-img" src={images[index].url} alt={`Slika ${index + 1}`} />

        {total > 1 && (
          <>
            <button className="lightbox-arrow lightbox-prev" aria-label="Prethodna"
              onClick={() => onChange((index - 1 + total) % total)}>&#8249;</button>
            <button className="lightbox-arrow lightbox-next" aria-label="Sledeca"
              onClick={() => onChange((index + 1) % total)}>&#8250;</button>
            <div className="lightbox-counter">{index + 1} / {total}</div>
          </>
        )}
      </div>
    </div>
  )
}
