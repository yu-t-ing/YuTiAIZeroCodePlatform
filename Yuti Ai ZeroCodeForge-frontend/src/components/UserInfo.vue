<template>
  <div class="user-info">
    <a-avatar :src="user?.userAvatar" :size="size">
      {{ user?.userName?.charAt(0) || 'U' }}
    </a-avatar>
    <span v-if="showName" class="user-name">{{ user?.userName || '未知用户' }}</span>
  </div>
</template>

<script setup lang="ts">
interface Props {
  user?: API.UserVO
  size?: number | 'small' | 'default' | 'large'
  showName?: boolean
}

withDefaults(defineProps<Props>(), {
  size: 'default',
  showName: true,
})
</script>

<style scoped>
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 14px;
  color: #ffffff;
  font-weight: 500;
}

/* 头像样式优化 */
.user-info :deep(.ant-avatar) {
  background: rgba(138, 124, 255, 0.2);
  border: 2px solid rgba(138, 124, 255, 0.4);
  color: #ffffff;
  font-weight: 600;
  transition: all 0.3s ease;
}

.user-info :deep(.ant-avatar:hover) {
  border-color: rgba(138, 124, 255, 0.8);
  box-shadow: 0 0 10px rgba(138, 124, 255, 0.4);
  transform: scale(1.05);
}
</style>
