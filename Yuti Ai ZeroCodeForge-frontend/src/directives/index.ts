import type { App } from 'vue'
import { reveal } from './revealDirective'

// 注册所有全局指令
export function setupDirectives(app: App) {
  app.directive('reveal', reveal)
}

// 导出单个指令，用于局部注册
export { reveal }
