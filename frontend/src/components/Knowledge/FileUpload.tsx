import { useState, useRef, type DragEvent, type ChangeEvent } from 'react';
import { getUploadUrl, createTask } from '../../services/knowledge';
import './FileUpload.css';

interface FileUploadProps {
  onUploadComplete: () => void;
}

export function FileUpload({ onUploadComplete }: FileUploadProps) {
  const [isDragging, setIsDragging] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFiles = async (files: FileList | null) => {
    if (!files || files.length === 0) return;

    const file = files[0];
    const allowedTypes = [
      'application/pdf',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'text/plain',
    ];

    if (!allowedTypes.includes(file.type)) {
      setError('仅支持 PDF、Word 和纯文本文件');
      return;
    }

    setError('');
    setUploading(true);

    try {
      const { uploadUrl, objectKey } = await getUploadUrl(file.name);

      await fetch(uploadUrl, {
        method: 'PUT',
        body: file,
        headers: { 'Content-Type': file.type },
      });

      await createTask(file.name, objectKey);
      onUploadComplete();
    } catch {
      setError('上传失败，请重试');
    } finally {
      setUploading(false);
    }
  };

  const handleDragOver = (e: DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => setIsDragging(false);

  const handleDrop = (e: DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    handleFiles(e.dataTransfer.files);
  };

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    handleFiles(e.target.files);
  };

  return (
    <div
      className={`file-upload ${isDragging ? 'file-upload--dragging' : ''}`}
      onDragOver={handleDragOver}
      onDragLeave={handleDragLeave}
      onDrop={handleDrop}
      onClick={() => fileInputRef.current?.click()}
      role="button"
      tabIndex={0}
    >
      <input
        ref={fileInputRef}
        type="file"
        accept=".pdf,.doc,.docx,.txt"
        onChange={handleChange}
        hidden
      />

      {uploading ? (
        <div className="file-upload__status">上传中...</div>
      ) : (
        <div className="file-upload__prompt">
          <span className="file-upload__icon">📄</span>
          <p>拖拽文件到此处或点击选择</p>
          <p className="file-upload__hint">支持 PDF、Word、TXT 格式</p>
        </div>
      )}

      {error && <div className="file-upload__error">{error}</div>}
    </div>
  );
}
