import { useState } from 'react';
import { FileUpload } from './FileUpload';
import { TaskList } from './TaskList';

export function KnowledgePage() {
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const handleUploadComplete = () => {
    setRefreshTrigger((prev) => prev + 1);
  };

  return (
    <div style={{ padding: '2rem', maxWidth: '900px', margin: '0 auto' }}>
      <h1>知识库管理</h1>
      <FileUpload onUploadComplete={handleUploadComplete} />
      <TaskList refreshTrigger={refreshTrigger} />
    </div>
  );
}
