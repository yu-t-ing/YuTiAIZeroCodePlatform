/**
 * 环境变量配置
 */
import {CodeGenTypeEnum} from "@/utils/codeGenTypes.ts";

// 应用部署域名
export const DEPLOY_DOMAIN = import.meta.env.VITE_DEPLOY_DOMAIN || 'http://localhost:8123'

// API 基础路径（用于 axios，支持相对路径）
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

// API 完整地址（用于 EventSource，必须是绝对路径）
// 在开发环境使用 localhost:8123，在生产环境使用当前域名
export const API_FULL_URL = import.meta.env.VITE_API_FULL_URL ||
  (import.meta.env.DEV ? 'http://localhost:8123/api' : `${window.location.origin}/api`)

// 静态资源地址（使用相对路径，避免跨域）
export const STATIC_BASE_URL = `${API_BASE_URL}/static`

// 获取部署应用的完整URL
export const getDeployUrl = (deployKey: string) => {
  return `${DEPLOY_DOMAIN}/${deployKey}`
}

// 获取静态资源预览URL
export const getStaticPreviewUrl = (codeGenType: string, appId: string) => {
  const baseUrl = `${STATIC_BASE_URL}/${codeGenType}_${appId}/`
  // 如果是 Vue 项目，浏览地址需要添加 dist 后缀
  if (codeGenType === CodeGenTypeEnum.VUE_PROJECT) {
    return `${baseUrl}dist/index.html`
  }
  // 对于 HTML 和多文件模式，直接访问 index.html
  return `${baseUrl}index.html`
}
