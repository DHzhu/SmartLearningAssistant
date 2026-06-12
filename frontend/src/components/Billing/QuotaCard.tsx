import { useState, useEffect } from 'react';
import { getBalance } from '../../services/billing';
import './QuotaCard.css';

export function QuotaCard() {
  const [balance, setBalance] = useState<number | null>(null);

  useEffect(() => {
    const fetchBalance = async () => {
      try {
        const data = await getBalance();
        setBalance(data.balance);
      } catch {
        // silently fail
      }
    };

    fetchBalance();
    const interval = setInterval(fetchBalance, 30000);
    return () => clearInterval(interval);
  }, []);

  if (balance === null) return null;

  const isLow = balance < 1000;

  return (
    <div className={`quota-card ${isLow ? 'quota-card--low' : ''}`}>
      <span className="quota-card__label">Token 余额</span>
      <span className="quota-card__value">{balance.toLocaleString()}</span>
      {isLow && <span className="quota-card__warning">余额不足</span>}
    </div>
  );
}
