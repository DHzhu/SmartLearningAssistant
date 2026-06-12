import { useState, useEffect, useCallback } from 'react';
import type { KnowledgeTask } from '../../types';
import { listTasks } from '../../services/knowledge';
import './TaskList.css';

interface TaskListProps {
  refreshTrigger: number;
}

const STATUS_LABELS: Record<string, string> = {
  PENDING: '等待处理',
  PROCESSING: '解析中',
  SUCCESS: '解析成功',
  FAILED: '解析失败',
};

const STATUS_CLASSES: Record<string, string> = {
  PENDING: 'status--pending',
  PROCESSING: 'status--processing',
  SUCCESS: 'status--success',
  FAILED: 'status--failed',
};

export function TaskList({ refreshTrigger }: TaskListProps) {
  const [tasks, setTasks] = useState<KnowledgeTask[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchTasks = useCallback(async () => {
    try {
      const data = await listTasks();
      setTasks(data);
    } catch {
      // silently fail
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTasks();
  }, [fetchTasks, refreshTrigger]);

  useEffect(() => {
    const hasProcessing = tasks.some(
      (t) => t.status === 'PENDING' || t.status === 'PROCESSING'
    );
    if (!hasProcessing) return;

    const interval = setInterval(fetchTasks, 3000);
    return () => clearInterval(interval);
  }, [tasks, fetchTasks]);

  if (loading) return <div className="task-list__loading">加载中...</div>;

  if (tasks.length === 0) {
    return <div className="task-list__empty">暂无上传记录</div>;
  }

  return (
    <div className="task-list">
      <h3 className="task-list__title">语料处理状态</h3>
      <table className="task-list__table">
        <thead>
          <tr>
            <th>文件名</th>
            <th>状态</th>
            <th>分块数</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          {tasks.map((task) => (
            <tr key={task.id}>
              <td>{task.filename}</td>
              <td>
                <span className={`status ${STATUS_CLASSES[task.status] ?? ''}`}>
                  {STATUS_LABELS[task.status] ?? task.status}
                </span>
              </td>
              <td>{task.chunkCount}</td>
              <td>{new Date(task.createdAt).toLocaleString('zh-CN')}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
