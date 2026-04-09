<template>
  <div class="custom-cursor">
    <div ref="cursorOuter" class="cursor-outer"></div>
    <div ref="cursorInner" class="cursor-inner"></div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'

const cursorOuter = ref<HTMLElement>()
const cursorInner = ref<HTMLElement>()

let mouseX = 0
let mouseY = 0
let outerX = 0
let outerY = 0
let innerX = 0
let innerY = 0

// 鼠标移动事件处理
const handleMouseMove = (e: MouseEvent) => {
  mouseX = e.clientX
  mouseY = e.clientY

  // 内圈立即跟随
  if (cursorInner.value) {
    innerX = mouseX
    innerY = mouseY
    cursorInner.value.style.transform = `translate(${innerX}px, ${innerY}px) translate(-50%, -50%)`
  }
}

// 动画循环
const animate = () => {
  // 外圈平滑跟随（有延迟效果）
  outerX += (mouseX - outerX) * 0.15
  outerY += (mouseY - outerY) * 0.15

  if (cursorOuter.value) {
    cursorOuter.value.style.transform = `translate(${outerX}px, ${outerY}px) translate(-50%, -50%)`
  }

  requestAnimationFrame(animate)
}

// 鼠标按下效果 - 点击时放大并隐藏内圈
const handleMouseDown = (e: MouseEvent) => {
  const target = e.target as HTMLElement
  const isClickable =
    target.tagName === 'A' ||
    target.tagName === 'BUTTON' ||
    target.classList.contains('ant-btn') ||
    target.onclick !== null ||
    target.closest('a') !== null ||
    target.closest('button') !== null ||
    target.closest('.ant-btn') !== null

  if (isClickable) {
    if (cursorOuter.value) {
      cursorOuter.value.style.transform += ' scale(1.5)'
    }
    if (cursorInner.value) {
      cursorInner.value.style.opacity = '0'
    }
  } else {
    if (cursorOuter.value) {
      cursorOuter.value.style.transform += ' scale(0.8)'
    }
    if (cursorInner.value) {
      cursorInner.value.style.transform += ' scale(0.8)'
    }
  }
}

// 鼠标抬起效果 - 恢复原状
const handleMouseUp = () => {
  if (cursorOuter.value) {
    cursorOuter.value.style.transform = cursorOuter.value.style.transform
      .replace(' scale(1.5)', '')
      .replace(' scale(0.8)', '')
  }
  if (cursorInner.value) {
    cursorInner.value.style.transform = cursorInner.value.style.transform.replace(' scale(0.8)', '')
    cursorInner.value.style.opacity = '1'
  }
}

onMounted(() => {
  // 只在桌面端启用
  if (window.innerWidth > 768) {
    document.addEventListener('mousemove', handleMouseMove)
    document.addEventListener('mousedown', handleMouseDown)
    document.addEventListener('mouseup', handleMouseUp)

    // 开始动画循环
    animate()
  }
})

onUnmounted(() => {
  if (window.innerWidth > 768) {
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mousedown', handleMouseDown)
    document.removeEventListener('mouseup', handleMouseUp)
  }
})
</script>

<style scoped>
.custom-cursor {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 9999;
}

.cursor-outer {
  position: absolute;
  width: 40px;
  height: 40px;
  border: 2px solid var(--primary-color);
  border-radius: 50%;
  transition: transform 0.15s ease-out, width 0.3s ease, height 0.3s ease,
    border-color 0.3s ease;
  will-change: transform;
}

.cursor-inner {
  position: absolute;
  width: 8px;
  height: 8px;
  background: var(--primary-color);
  border-radius: 50%;
  transition: transform 0.05s ease-out, opacity 0.3s ease;
  will-change: transform;
}

/* 移动端隐藏自定义光标 */
@media (max-width: 768px) {
  .custom-cursor {
    display: none;
  }
}
</style>
