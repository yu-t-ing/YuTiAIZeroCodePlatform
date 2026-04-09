<template>
  <Card3D>
    <div class="app-card" :class="{ 'app-card--featured': featured }">
      <div class="card-glow"></div>
      <div class="gradient-border"></div>
      <div class="app-preview">
        <img v-if="app.cover" :src="app.cover" :alt="app.appName" />
        <div v-else class="bot-preview">
          <div class="bot-ring"></div>
          <div class="bot-core">
            <span class="bot-eye"></span>
            <span class="bot-mouth"></span>
          </div>
          <div class="bot-shadow"></div>
        </div>
        <div class="app-overlay">
          <div class="overlay-content">
            <p class="overlay-desc">{{ app.appDesc || '智能生成的多场景应用，支持对话、部署与个性化配置。' }}</p>
            <div class="overlay-actions">
              <a-button type="primary" ghost @click.stop="handleViewChat">查看对话</a-button>
              <a-button v-if="app.deployKey" type="default" @click.stop="handleViewWork">查看作品</a-button>
            </div>
          </div>
        </div>
      </div>
      <div class="app-info">
        <div class="app-info-left">
          <a-avatar :src="app.user?.userAvatar" :size="48">
            {{ app.user?.userName?.charAt(0) || 'U' }}
          </a-avatar>
        </div>
        <div class="app-info-right">
          <h3 class="app-title gradient-text">{{ app.appName || '未命名应用' }}</h3>
          <p class="app-author">
            {{ app.user?.userName || (featured ? '官方精选' : '未知用户') }}
          </p>
          <div class="app-tags">
            <span class="app-tag gradient-tag">{{ featured ? '精选' : '我的' }}</span>
            <span class="app-tag app-tag--pulse">AI 生成</span>
          </div>
        </div>
      </div>
    </div>
  </Card3D>
</template>

<script setup lang="ts">
import Card3D from '@/components/Card3D.vue'

interface Props {
  app: API.AppVO
  featured?: boolean
}

interface Emits {
  (e: 'view-chat', appId: string | number | undefined): void
  (e: 'view-work', app: API.AppVO): void
}

const props = withDefaults(defineProps<Props>(), {
  featured: false,
})

const emit = defineEmits<Emits>()

const handleViewChat = () => {
  emit('view-chat', props.app.id)
}

const handleViewWork = () => {
  emit('view-work', props.app)
}
</script>

<style scoped>
.app-card {
  position: relative;
  background: var(--card-bg);
  border-radius: var(--border-radius-md);
  overflow: hidden;
  border: 2px solid transparent;
  background-clip: padding-box;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: var(--shadow-sm);
  transition: box-shadow var(--duration-normal) var(--ease-bounce),
              background var(--duration-normal) var(--ease-bounce);
  cursor: pointer;
  transform-style: preserve-3d;
}

.gradient-border {
  position: absolute;
  inset: 0;
  border-radius: var(--border-radius-md);
  padding: 2px;
  background: var(--gradient-primary);
  -webkit-mask:
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0.3;
  transition: opacity var(--duration-normal) var(--ease-smooth);
  pointer-events: none;
}

.card-glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, var(--primary-color), transparent 60%);
  opacity: 0;
  transition: opacity var(--duration-normal) var(--ease-smooth);
  pointer-events: none;
  z-index: 0;
}

.app-card:hover {
  box-shadow: var(--shadow-lg);
  background: var(--card-bg-hover);
}

.app-card:hover .gradient-border {
  opacity: 0.8;
}

.app-card:hover .card-glow {
  opacity: 0.3;
}

.gradient-text {
  background: var(--gradient-text);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 700;
}

.gradient-tag {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  border-color: var(--primary-color);
  font-weight: 600;
}

.app-preview {
  height: 200px;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
  opacity: 0.9;
}

.app-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--duration-slow) var(--ease-smooth);
}

.app-card:hover .app-preview img {
  transform: scale(1.08);
}

.bot-preview {
  position: relative;
  width: 140px;
  height: 140px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.3), transparent 70%);
  animation: float 4s ease-in-out infinite;
}

.bot-ring {
  position: absolute;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 1px solid var(--primary-color);
  animation: rotate 10s linear infinite;
}

.bot-core {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  background: linear-gradient(135deg, var(--bg-tertiary), var(--bg-secondary));
  border: 1px solid var(--primary-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  position: relative;
}

.bot-eye,
.bot-mouth {
  display: block;
  background: var(--primary-color);
  border-radius: 999px;
  box-shadow: 0 0 12px var(--primary-color);
}

.bot-eye {
  width: 28px;
  height: 6px;
}

.bot-mouth {
  width: 40px;
  height: 4px;
  opacity: 0.6;
}

.bot-shadow {
  position: absolute;
  width: 120px;
  height: 20px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.2), transparent 70%);
  bottom: 0;
  transform: translateY(60px);
}

.app-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(10, 10, 10, 0.3), rgba(10, 10, 10, 0.85));
  display: flex;
  align-items: flex-end;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--duration-normal) var(--ease-smooth);
  padding: 24px;
}

.app-card:hover .app-overlay {
  opacity: 1;
}

.overlay-content {
  width: 100%;
}

.overlay-desc {
  color: #ffffff;
  margin: 0 0 16px;
  font-size: 14px;
  line-height: 1.5;
  opacity: 0.95;
}

.overlay-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.overlay-actions :deep(.ant-btn) {
  border-radius: var(--border-radius-full);
  padding: 0 18px;
  height: 40px;
  transition: all var(--duration-fast) var(--ease-bounce);
}

.overlay-actions :deep(.ant-btn:hover) {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
}

.overlay-actions :deep(.ant-btn-primary) {
  background: var(--gradient-primary);
  border: none;
  color: #ffffff !important;
}

.overlay-actions :deep(.ant-btn-default) {
  border-color: var(--primary-color);
  color: #ffffff !important;
  background: rgba(138, 124, 255, 0.2);
}

.app-info {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  background: var(--bg-secondary);
}

.app-info-left {
  flex-shrink: 0;
}

.app-info-left :deep(.ant-avatar) {
  border: 2px solid var(--primary-color);
  background: var(--bg-tertiary);
  color: var(--text-primary);
  transition: all var(--duration-normal) var(--ease-bounce);
}

.app-info-left :deep(.ant-avatar:hover) {
  transform: scale(1.1);
  box-shadow: var(--shadow-sm);
}

.app-info-right {
  flex: 1;
  min-width: 0;
}

.app-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 4px;
  color: #ffffff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.app-author {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  margin: 0 0 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.app-tags {
  display: flex;
  gap: 8px;
}

.app-tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: var(--border-radius-full);
  border: 1px solid var(--card-border);
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.08);
  transition: all var(--duration-fast) var(--ease-bounce);
  cursor: default;
}

.app-tag:hover {
  transform: translateY(-3px);
  background: rgba(138, 124, 255, 0.2);
  border-color: var(--primary-color);
  color: #ffffff;
}

.app-tag--pulse {
  border-color: var(--accent-color);
  color: var(--accent-color);
  animation: pulse 2s infinite;
}

.app-card--featured {
  border-color: var(--primary-color);
  box-shadow: var(--shadow-md);
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(102, 126, 234, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(102, 126, 234, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(102, 126, 234, 0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-preview {
    height: 160px;
  }

  .bot-preview {
    width: 100px;
    height: 100px;
  }

  .bot-ring {
    width: 90px;
    height: 90px;
  }

  .bot-core {
    width: 56px;
    height: 56px;
  }

  .app-info {
    padding: 16px;
  }

  .app-title {
    font-size: 16px;
  }

  .app-author {
    font-size: 13px;
  }
}
</style>
