export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: number;
  username: string;
  role: string;
}

export interface User {
  userId: number;
  username: string;
  role: string;
}

export interface KnowledgeTask {
  id: number;
  userId: number;
  filename: string;
  s3Url: string;
  status: 'PENDING' | 'PROCESSING' | 'SUCCESS' | 'FAILED';
  errorMessage: string | null;
  chunkCount: number;
  createdAt: string;
  updatedAt: string;
}
