import type { Directive } from 'vue'

/**
 * 滚动淡入上升动画指令
 * 用法: v-reveal
 * 可选参数: v-reveal="{ delay: 200, distance: 50 }"
 */
export const reveal: Directive = {
  mounted(el: HTMLElement, binding) {
    // 获取配置参数
    const options = binding.value || {}
    const delay = options.delay || 0
    const distance = options.distance || 50

    // 初始化元素状态
    el.style.opacity = '0'
    el.style.transform = `translateY(${distance}px)`
    el.style.transition = `opacity 0.8s ease ${delay}ms, transform 0.8s cubic-bezier(0.175, 0.885, 0.32, 1.275) ${delay}ms`

    // 创建观察器
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            // 元素进入视口，触发动画
            el.style.opacity = '1'
            el.style.transform = 'translateY(0)'
            // 动画完成后停止观察
            observer.unobserve(el)
          }
        })
      },
      {
        threshold: 0.1, // 元素出现10%时触发
        rootMargin: '0px 0px -50px 0px',
      }
    )

    // 开始观察
    observer.observe(el)

    // 保存观察器实例，用于后续清理
    ;(el as any)._revealObserver = observer
  },

  unmounted(el: HTMLElement) {
    // 清理观察器
    const observer = (el as any)._revealObserver
    if (observer) {
      observer.disconnect()
    }
  },
}

// 默认导出
export default reveal
