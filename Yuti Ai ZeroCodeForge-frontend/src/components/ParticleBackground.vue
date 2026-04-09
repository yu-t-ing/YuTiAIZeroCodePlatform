<template>
  <canvas ref="canvas" class="particle-canvas"></canvas>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'

const canvas = ref<HTMLCanvasElement>()
let ctx: CanvasRenderingContext2D | null = null
let particles: Star[] = []
let animationId: number = 0

// 鼠标位置
let mouseX = 0
let mouseY = 0
const mouseRadius = 180 // 鼠标影响半径

// 星星类
class Star {
  x: number
  y: number
  vx: number
  vy: number
  size: number
  baseAlpha: number
  alpha: number
  twinkleSpeed: number
  twinklePhase: number

  constructor(canvasWidth: number, canvasHeight: number) {
    this.x = Math.random() * canvasWidth
    this.y = Math.random() * canvasHeight

    // 随机速度，更慢更优雅
    this.vx = (Math.random() - 0.5) * 0.2
    this.vy = (Math.random() - 0.5) * 0.2

    // 星星大小变化更大
    this.size = Math.random() * 2 + 0.5

    // 基础透明度和闪烁参数
    this.baseAlpha = Math.random() * 0.3 + 0.4
    this.alpha = this.baseAlpha
    this.twinkleSpeed = Math.random() * 0.02 + 0.005
    this.twinklePhase = Math.random() * Math.PI * 2
  }

  update(canvasWidth: number, canvasHeight: number, time: number) {
    // 移动
    this.x += this.vx
    this.y += this.vy

    // 边界检测，星星从另一侧出现
    if (this.x < -this.size) this.x = canvasWidth + this.size
    if (this.x > canvasWidth + this.size) this.x = -this.size
    if (this.y < -this.size) this.y = canvasHeight + this.size
    if (this.y > canvasHeight + this.size) this.y = -this.size

    // 闪烁效果 - 使用正弦波产生平滑的闪烁
    this.alpha = this.baseAlpha + Math.sin(time * this.twinkleSpeed + this.twinklePhase) * 0.2
    this.alpha = Math.max(0.2, Math.min(1, this.alpha))
  }

  draw(context: CanvasRenderingContext2D) {
    context.save()

    // 绘制光晕
    const gradient = context.createRadialGradient(
      this.x,
      this.y,
      0,
      this.x,
      this.y,
      this.size * 3
    )
    gradient.addColorStop(0, `rgba(255, 255, 255, ${this.alpha})`)
    gradient.addColorStop(0.4, `rgba(200, 200, 255, ${this.alpha * 0.5})`)
    gradient.addColorStop(1, 'rgba(200, 200, 255, 0)')

    context.fillStyle = gradient
    context.beginPath()
    context.arc(this.x, this.y, this.size * 3, 0, Math.PI * 2)
    context.fill()

    // 绘制星星核心
    context.fillStyle = `rgba(255, 255, 255, ${this.alpha})`
    context.shadowColor = 'rgba(255, 255, 255, 0.8)'
    context.shadowBlur = this.size * 2
    context.beginPath()
    context.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    context.fill()

    context.restore()
  }
}

// 初始化星星
const initStars = (width: number, height: number) => {
  particles = []
  // 根据屏幕面积动态计算星星数量
  const area = width * height
  const starCount = Math.floor(area / 12000) // 每12000像素一个星星

  for (let i = 0; i < starCount; i++) {
    particles.push(new Star(width, height))
  }
}

// 绘制星星之间的连线
const drawStarLines = () => {
  if (!ctx) return

  const maxDistance = 120

  for (let i = 0; i < particles.length; i++) {
    for (let j = i + 1; j < particles.length; j++) {
      const dx = particles[i].x - particles[j].x
      const dy = particles[i].y - particles[j].y
      const distance = Math.sqrt(dx * dx + dy * dy)

      if (distance < maxDistance) {
        // 距离越近，线越亮
        const alpha = (1 - distance / maxDistance) * 0.25

        ctx.save()
        ctx.globalAlpha = alpha
        ctx.strokeStyle = '#667eea'
        ctx.lineWidth = 1
        ctx.beginPath()
        ctx.moveTo(particles[i].x, particles[i].y)
        ctx.lineTo(particles[j].x, particles[j].y)
        ctx.stroke()
        ctx.restore()
      }
    }
  }
}

// 绘制鼠标与星星的连线和吸引效果
const drawMouseEffects = () => {
  if (!ctx || mouseX === 0 || mouseY === 0) return

  particles.forEach((star) => {
    const dx = star.x - mouseX
    const dy = star.y - mouseY
    const distance = Math.sqrt(dx * dx + dy * dy)

    if (distance < mouseRadius) {
      // 绘制连线
      const alpha = (1 - distance / mouseRadius) * 0.5
      ctx.save()
      ctx.globalAlpha = alpha
      ctx.strokeStyle = '#667eea'
      ctx.lineWidth = 1.5
      ctx.beginPath()
      ctx.moveTo(star.x, star.y)
      ctx.lineTo(mouseX, mouseY)
      ctx.stroke()
      ctx.restore()

      // 轻微的吸引效果
      const attractionForce = 0.0003
      const angle = Math.atan2(mouseY - star.y, mouseX - star.x)
      star.vx += Math.cos(angle) * attractionForce
      star.vy += Math.sin(angle) * attractionForce

      // 限制最大速度
      const maxSpeed = 0.5
      const speed = Math.sqrt(star.vx * star.vx + star.vy * star.vy)
      if (speed > maxSpeed) {
        star.vx = (star.vx / speed) * maxSpeed
        star.vy = (star.vy / speed) * maxSpeed
      }
    }
  })
}

// 鼠标移动事件
const handleMouseMove = (e: MouseEvent) => {
  mouseX = e.clientX
  mouseY = e.clientY
}

// 鼠标离开时清除位置
const handleMouseLeave = () => {
  mouseX = 0
  mouseY = 0
}

// 动画循环
const animate = (time: number) => {
  if (!canvas.value || !ctx) return

  const width = canvas.value.width
  const height = canvas.value.height

  // 清空画布
  ctx.clearRect(0, 0, width, height)

  // 更新和绘制星星
  particles.forEach((star) => {
    star.update(width, height, time)
    star.draw(ctx)
  })

  // 绘制星星之间的连线
  drawStarLines()

  // 绘制鼠标效果
  drawMouseEffects()

  animationId = requestAnimationFrame(animate)
}

// 调整画布大小
const resizeCanvas = () => {
  if (!canvas.value) return

  canvas.value.width = window.innerWidth
  canvas.value.height = window.innerHeight

  initStars(canvas.value.width, canvas.value.height)
}

onMounted(() => {
  if (!canvas.value) return

  ctx = canvas.value.getContext('2d')
  if (!ctx) return

  resizeCanvas()
  window.addEventListener('resize', resizeCanvas)
  window.addEventListener('mousemove', handleMouseMove, { passive: true })
  window.addEventListener('mouseout', handleMouseLeave, { passive: true })

  animate(0)
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeCanvas)
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseout', handleMouseLeave)
  cancelAnimationFrame(animationId)
})
</script>

<style scoped>
.particle-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}
</style>
