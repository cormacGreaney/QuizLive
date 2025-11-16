// Small button that opens QrModal for a given quizId.

import React, { useMemo, useState } from 'react';
import QrModal from './QrModal';

type Props = {
  quizId: number | string;
  buildUrl?: (quizId: number | string) => string;
};

export default function QrButton({ quizId, buildUrl }: Props) {
  const [open, setOpen] = useState(false);

  const url = useMemo(() => {
    const mk = buildUrl ?? ((id: number | string) => `${window.location.origin}/play/${id}`);
    return mk(quizId);
  }, [quizId, buildUrl]);

  return (
    <>
      <button onClick={() => setOpen(true)} style={{ padding: '6px 10px', cursor: 'pointer' }}>
        Show QR
      </button>
      <QrModal
        open={open}
        onClose={() => setOpen(false)}
        url={url}
        title="Scan to join"
      />
    </>
  );
}
