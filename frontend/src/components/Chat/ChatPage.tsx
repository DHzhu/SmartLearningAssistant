import { useState, useRef, useEffect } from 'react';
import type { ChatMessage } from '../../services/chat';
import { sendChatStream } from '../../services/chat';
import { QuotaCard } from '../Billing/QuotaCard';
import './ChatPage.css';

export function ChatPage() {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState('');
  const [isStreaming, setIsStreaming] = useState(false);
  const [error, setError] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const abortRef = useRef<AbortController | null>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = () => {
    const text = input.trim();
    if (!text || isStreaming) return;

    setError('');
    setInput('');
    setIsStreaming(true);

    const userMessage: ChatMessage = { role: 'user', content: text };
    const assistantMessage: ChatMessage = { role: 'assistant', content: '' };

    setMessages((prev) => [...prev, userMessage, assistantMessage]);

    abortRef.current = sendChatStream(text, {
      onChunk: (chunk) => {
        setMessages((prev) => {
          const updated = [...prev];
          const last = updated[updated.length - 1];
          if (last.role === 'assistant') {
            updated[updated.length - 1] = {
              ...last,
              content: last.content + chunk,
            };
          }
          return updated;
        });
      },
      onSources: (sources) => {
        setMessages((prev) => {
          const updated = [...prev];
          const last = updated[updated.length - 1];
          if (last.role === 'assistant') {
            updated[updated.length - 1] = { ...last, sources };
          }
          return updated;
        });
      },
      onDone: () => setIsStreaming(false),
      onError: (errMsg) => {
        setError(errMsg);
        setIsStreaming(false);
      },
    });
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="chat-page">
      <div className="chat-page__header">
        <h1>智能对话</h1>
        <QuotaCard />
      </div>

      <div className="chat-page__messages">
        {messages.length === 0 && (
          <div className="chat-page__empty">
            <p>👋 你好！我是你的智能学习助手</p>
            <p>基于你的知识库进行对话，输入问题开始吧</p>
          </div>
        )}

        {messages.map((msg, i) => (
          <div key={i} className={`chat-bubble chat-bubble--${msg.role}`}>
            <div className="chat-bubble__content">{msg.content}</div>
            {msg.sources && msg.sources.length > 0 && (
              <div className="chat-bubble__sources">
                参考来源：{msg.sources.join(', ')}
              </div>
            )}
          </div>
        ))}

        {isStreaming && (
          <div className="chat-typing">
            <span className="chat-typing__dot" />
            <span className="chat-typing__dot" />
            <span className="chat-typing__dot" />
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {error && <div className="chat-page__error">{error}</div>}

      <div className="chat-page__input">
        <textarea
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="输入你的问题..."
          rows={1}
          disabled={isStreaming}
        />
        <button onClick={handleSend} disabled={isStreaming || !input.trim()}>
          发送
        </button>
      </div>
    </div>
  );
}
