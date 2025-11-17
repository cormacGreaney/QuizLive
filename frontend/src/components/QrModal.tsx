import React, { useEffect } from 'react';
import { createPortal } from 'react-dom';
import { QRCodeSVG } from 'qrcode.react';

type Props = { open: boolean; onClose: () => void; url: string; title?: string };

export default function QRModal({ open, onClose, url, title = 'Share this quiz' }: Props) {
  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => e.key === 'Escape' && onClose();
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [open, onClose]);

  if (!open) return null;

  const modal = (
    <div style={backdrop} onClick={onClose} role="dialog" aria-modal="true">
      <div style={sheet} onClick={(e) => e.stopPropagation()}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 style={{ margin: 0 }}>{title}</h3>
          <button onClick={onClose} style={iconBtn} aria-label="Close">✕</button>
        </div>

        <p style={{ color: '#555', marginTop: 8, marginBottom: 16 }}>
          Scan this with your phone or share the link below.
        </p>

        <div style={{ display: 'grid', placeItems: 'center', marginBottom: 16 }}>
          <QRCodeSVG value={url} size={220} includeMargin />
        </div>

        <div style={{ display: 'flex', gap: 8 }}>
          <input
            value={url}
            readOnly
            style={{
              flex: 1, padding: '8px 10px', border: '1px solid #d1d5db',
              borderRadius: 8, fontFamily: 'monospace', fontSize: 13,
            }}
            aria-label="Quiz URL"
            onFocus={(e) => e.currentTarget.select()}
          />
          <button
            style={btn}
            onClick={() => navigator.clipboard.writeText(url).then(() => alert('Link copied'))}
          >
            Copy
          </button>
        </div>
      </div>
    </div>
  );

  // Render to <body> to avoid table nesting issues.
  return createPortal(modal, document.body);
}

const backdrop: React.CSSProperties = {
  position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.45)', display: 'grid', placeItems: 'center', zIndex: 60,
};
const sheet: React.CSSProperties = {
  width: 'min(92vw, 480px)', background: '#fff', borderRadius: 12, padding: 16,
  border: '1px solid #e5e7eb', boxShadow: '0 10px 30px rgba(0,0,0,0.25)',
};
const btn: React.CSSProperties = {
  padding: '8px 12px', border: '1px solid #111827', background: '#111827', color: '#fff', borderRadius: 8, cursor: 'pointer',
};
const iconBtn: React.CSSProperties = {
  ...btn, padding: '6px 10px', background: '#fff', color: '#111827', borderColor: '#d1d5db',
};
