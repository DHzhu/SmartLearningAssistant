import { useState, useEffect } from 'react';
import type { BillingLog } from '../../types';
import { getBalance, recharge, getBillingHistory } from '../../services/billing';
import './BillingPage.css';

export function BillingPage() {
  const [balance, setBalance] = useState(0);
  const [history, setHistory] = useState<BillingLog[]>([]);
  const [rechargeAmount, setRechargeAmount] = useState('');

  const fetchData = async () => {
    try {
      const [balanceData, historyData] = await Promise.all([
        getBalance(),
        getBillingHistory(),
      ]);
      setBalance(balanceData.balance);
      setHistory(historyData);
    } catch {
      // silently fail
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleRecharge = async () => {
    const amount = parseInt(rechargeAmount, 10);
    if (isNaN(amount) || amount <= 0) return;

    try {
      const data = await recharge(amount);
      setBalance(data.balance);
      setRechargeAmount('');
      fetchData();
    } catch {
      // silently fail
    }
  };

  return (
    <div className="billing-page">
      <h1>配额管理</h1>

      <div className="billing-page__balance-section">
        <div className="billing-page__balance">
          <span className="billing-page__balance-label">当前余额</span>
          <span className="billing-page__balance-value">{balance.toLocaleString()} Tokens</span>
        </div>

        <div className="billing-page__recharge">
          <input
            type="number"
            value={rechargeAmount}
            onChange={(e) => setRechargeAmount(e.target.value)}
            placeholder="充值数量"
            min="1"
          />
          <button onClick={handleRecharge}>充值</button>
        </div>
      </div>

      <h2>扣费记录</h2>
      <table className="billing-page__table">
        <thead>
          <tr>
            <th>类型</th>
            <th>数量</th>
            <th>描述</th>
            <th>时间</th>
          </tr>
        </thead>
        <tbody>
          {history.map((log) => (
            <tr key={log.id}>
              <td>
                <span className={`billing-type billing-type--${log.type.toLowerCase()}`}>
                  {log.type === 'DEDUCT' ? '扣费' : '充值'}
                </span>
              </td>
              <td className={log.type === 'DEDUCT' ? 'amount--deduct' : 'amount--recharge'}>
                {log.type === 'DEDUCT' ? '-' : '+'}{log.amount.toLocaleString()}
              </td>
              <td>{log.description}</td>
              <td>{new Date(log.createdAt).toLocaleString('zh-CN')}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
