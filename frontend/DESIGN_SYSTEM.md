# 前端设计系统 - 精致未来主义

## 设计理念

Smart Learning Assistant 采用"精致未来主义"(Refined Futurism)美学方向，为智能学习平台创造独特而专业的视觉识别度。

### 核心原则

- **智能而不冷冰冰** - 体现 AI 的智能感，但保持温度和亲和力
- **专业而不乏味** - 适合企业 SaaS，但避免传统管理后台的枯燥
- **极简而不简单** - 克制的用色和布局，但细节精致
- **难忘而不俗套** - 独特的视觉识别度，避免通用 AI 产品的套路

## 字体系统

### 主字体搭配
- **显示字体**: `Space Grotesk` (标题、数字、重要文本)
- **正文字体**: `Plus Jakarta Sans` (正文、界面文本)

### 使用规则
```css
/* 标题和重要文本 */
h1, h2, h3, .display-text {
  font-family: 'Space Grotesk', sans-serif;
  font-weight: 600;
  letter-spacing: -0.02em;
}

/* 正文和界面文本 */
body, .body-text {
  font-family: 'Plus Jakarta Sans', sans-serif;
  font-weight: 400;
}
```

## 色彩系统

### 主色调
```css
--bg-deep: #0a0e1a;        /* 深邃背景 */
--bg-surface: #111827;     /* 表面背景 */
--bg-elevated: #1a2332;     /* 提升元素背景 */
```

### 文本色彩
```css
--text-primary: #f1f5f9;    /* 主要文本 */
--text-secondary: #94a3b8;  /* 次要文本 */
--text-muted: #64748b;     /* 弱化文本 */
```

### 主题色
```css
--accent-amber: #f59e0b;           /* 琥珀金 - 主要强调色 */
--accent-amber-glow: rgba(245, 158, 11, 0.15);
--accent-teal: #14b8a6;            /* 青色 - 次要强调色 */
--accent-teal-glow: rgba(20, 184, 166, 0.15);
```

### 语义色
```css
--success-base: #10b981;          /* 成功 */
--success-bg: rgba(16, 185, 129, 0.1);
--error-base: #ef4444;             /* 错误 */
--error-bg: rgba(239, 68, 68, 0.1);
--warning-base: #f59e0b;           /* 警告 */
--warning-bg: rgba(245, 158, 11, 0.1);
```

### 边框和分隔
```css
--border-subtle: rgba(148, 163, 184, 0.08);   /* 微妙边框 */
--border-default: rgba(148, 163, 184, 0.12);   /* 默认边框 */
--border-strong: rgba(148, 163, 184, 0.2);     /* 强调边框 */
```

## 阴影系统

```css
--shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.3);
--shadow-md: 0 4px 6px rgba(0, 0, 0, 0.4);
--shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.5);
--shadow-glow-amber: 0 0 20px rgba(245, 158, 11, 0.3);
--shadow-glow-teal: 0 0 20px rgba(20, 184, 166, 0.3);
```

## 动画系统

### 缓动函数
```css
--transition-fast: 150ms cubic-bezier(0.4, 0, 0.2, 1);
--transition-base: 250ms cubic-bezier(0.4, 0, 0.2, 1);
--transition-slow: 350ms cubic-bezier(0.4, 0, 0.2, 1);
```

### 关键动画
- **fadeIn**: 淡入向上 (0.6s)
- **slideDown**: 从上滑入 (0.5s)
- **slideUp**: 从下滑入 (0.5s)
- **slideInLeft**: 从左滑入 (0.4s)
- **slideInRight**: 从右滑入 (0.4s)
- **float**: 浮动效果 (3s, infinite)
- **typing**: 输入指示器 (1.4s, infinite)
- **pulse**: 脉冲效果 (2s, infinite)
- **spin**: 旋转效果 (0.8s, infinite)

## 组件设计规范

### 按钮组件

#### 主要按钮
```css
.primary-button {
  background: linear-gradient(135deg, var(--accent-amber) 0%, #d97706 100%);
  color: var(--bg-deep);
  border-radius: 12px;
  padding: 0.875rem 1.75rem;
  font-weight: 600;
  box-shadow: var(--shadow-glow-amber);
}
```

#### 次要按钮
```css
.secondary-button {
  background: linear-gradient(135deg, var(--accent-teal) 0%, #0d9488 100%);
  color: var(--bg-deep);
  border-radius: 12px;
  padding: 0.875rem 1.75rem;
  font-weight: 600;
  box-shadow: var(--shadow-glow-teal);
}
```

### 卡片组件
```css
.card {
  background: rgba(26, 35, 50, 0.6);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-subtle);
  border-radius: 16px;
  box-shadow: var(--shadow-lg);
}
```

### 输入组件
```css
.input {
  background: rgba(10, 14, 26, 0.8);
  border: 1px solid var(--border-default);
  border-radius: 12px;
  color: var(--text-primary);
  transition: all var(--transition-base);
}

.input:focus {
  border-color: var(--accent-amber);
  box-shadow: 0 0 0 3px var(--accent-amber-glow);
}
```

## 视觉效果

### 全局噪点纹理
所有页面都应用了微妙的噪点纹理，增加质感和深度。

### 渐变效果
- **标题渐变**: 从主文本色到强调色的线性渐变
- **背景渐变**: 径向渐变用于创造深度和氛围
- **边框渐变**: 顶边和底边的渐变线条增加精致感

### 光晕效果
关键元素使用发光效果创造科技感和重要性。

### 模糊背景
使用 `backdrop-filter: blur()` 创造现代的毛玻璃效果。

## 响应式设计

### 断点
- 移动设备: < 768px
- 平板设备: 768px - 1024px
- 桌面设备: > 1024px

### 移动端优化
- 导航栏在移动端只显示图标
- 卡片和按钮在小屏幕上调整尺寸
- 表格在移动端调整字体大小和内边距

## 使用指南

### 添加新组件
1. 遵循现有的颜色系统和字体规范
2. 使用预定义的动画和过渡
3. 确保适当的响应式设计
4. 添加适当的视觉反馈（hover, focus 状态）

### 自定义样式
- 优先使用 CSS 变量而非硬编码颜色
- 遵循现有的命名约定 (BEM 方法论)
- 确保适当的对比度和可访问性

### 性能考虑
- 使用 CSS 动画而非 JavaScript 动画
- 优先使用 `transform` 和 `opacity` 属性
- 避免过多的渐变和阴影效果

## 设计资产

### 字体导入
```html
<link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&family=Space+Grotesk:wght@500;600;700&display=swap" rel="stylesheet">
```

### 图标使用
- 使用 Emoji 作为界面图标，保持轻量级
- 确保图标与文本的对齐和间距一致

### 品牌元素
- **Logo**: 🧠 (大脑图标，象征智能和学习)
- **主要颜色**: 琥珀金 (#f59e0b)
- **次要颜色**: 青色 (#14b8a6)

这个设计系统确保了整个应用的视觉一致性和专业感，同时保持独特和难忘的个性。