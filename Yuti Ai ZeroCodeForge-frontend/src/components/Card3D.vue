<template>
  <div
    ref="cardRef"
    class="card-3d-wrapper"
    @mousemove="handleMouseMove"
    @mouseleave="handleMouseLeave"
  >
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const cardRef = ref<HTMLElement>()

const handleMouseMove = (e: MouseEvent) => {
  if (!cardRef.value) return

  const rect = cardRef.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top

  const centerX = rect.width / 2
  const centerY = rect.height / 2

  // 计算旋转角度（除以10，参考实现）
  const rotateX = (y - centerY) / 10
  const rotateY = (centerX - x) / 10

  // 直接应用transform，包括perspective
  cardRef.value.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) translateY(-10px) scale3d(1.02, 1.02, 1.02)`
}

const handleMouseLeave = () => {
  if (!cardRef.value) return

  // 重置transform
  cardRef.value.style.transform = 'perspective(1000px) rotateX(0) rotateY(0) translateY(0) scale3d(1, 1, 1)'
}
</script>

<style scoped>
.card-3d-wrapper {
  transform-style: preserve-3d;
  transition: transform 0.1s ease-out, box-shadow 0.3s ease;
  pointer-events: auto !important;
}

.card-3d-wrapper:hover {
  box-shadow: 0 30px 60px rgba(102, 126, 234, 0.3);
}
</style>
