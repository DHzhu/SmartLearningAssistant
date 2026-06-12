export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  sources?: string[];
}

interface StreamCallbacks {
  onChunk: (chunk: string) => void;
  onSources: (sources: string[]) => void;
  onDone: () => void;
  onError: (error: string) => void;
}

export function sendChatStream(
  message: string,
  callbacks: StreamCallbacks
): AbortController {
  const controller = new AbortController();
  const token = sessionStorage.getItem('jwt_token');

  fetch('/api/chat/stream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({ message }),
    signal: controller.signal,
  })
    .then(async (response) => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      const reader = response.body?.getReader();
      if (!reader) throw new Error('No reader');

      const decoder = new TextDecoder();
      let buffer = '';

      while (true) {
        const { done, value } = await reader.read();
        if (done) break;

        buffer += decoder.decode(value, { stream: true });
        const lines = buffer.split('\n');
        buffer = lines.pop() ?? '';

        for (const line of lines) {
          if (line.startsWith('event:')) {
            continue;
          }
          if (line.startsWith('data:')) {
            const data = line.slice(5).trim();
            // Parse SSE event type from the preceding event: line
            // For simplicity, we detect based on data content
            if (data === '') {
              callbacks.onDone();
              return;
            }
            try {
              const parsed = JSON.parse(data);
              if (parsed.error === 'INSUFFICIENT_BALANCE') {
                callbacks.onError('余额不足，请充值');
                return;
              }
            } catch {
              // Not JSON, treat as message chunk
              callbacks.onChunk(data);
            }
          }
        }
      }
      callbacks.onDone();
    })
    .catch((err) => {
      if (err.name !== 'AbortError') {
        callbacks.onError(err.message);
      }
    });

  return controller;
}
