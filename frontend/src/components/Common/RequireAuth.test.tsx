import { describe, it, expect } from 'vitest';
import { RequireAuth } from './RequireAuth';

describe('RequireAuth', () => {
  it('should be a defined component', () => {
    expect(RequireAuth).toBeDefined();
    expect(typeof RequireAuth).toBe('function');
  });
});
