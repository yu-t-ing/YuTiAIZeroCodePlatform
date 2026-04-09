<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { addApp, listMyAppVoByPage, listGoodAppVoByPage } from '@/api/appController'
import { getDeployUrl } from '@/config/env'
import { CODE_GEN_TYPE_OPTIONS, CodeGenTypeEnum } from '@/utils/codeGenTypes'
import AppCard from '@/components/AppCard.vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()
let typingTimer: number | undefined

// 用户提示词
const userPrompt = ref('')
const selectedCodeGenType = ref<CodeGenTypeEnum>(CodeGenTypeEnum.MULTI_FILE)
const creating = ref(false)
const isTypingPulse = ref(false)
const pageReady = ref(false)

// 我的应用数据
const myApps = ref<API.AppVO[]>([])
const myAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})
const isMyAppsLoading = ref(true)

// 精选应用数据
const featuredApps = ref<API.AppVO[]>([])
const featuredAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})
const isFeaturedLoading = ref(true)

const templatePresets = [
  {
    id: 'blog',
    label: '个人博客网站',
    badge: '创作者',
    prompt:
      '创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局，文章支持Markdown格式，首页展示最新文章和热门推荐。',
  },
  {
    id: 'enterprise',
    label: '企业官网',
    badge: '品牌',
    prompt:
      '设计一个专业的企业官网，包含公司介绍、产品服务展示、新闻资讯、联系我们等页面。采用商务风格的设计，包含轮播图、产品展示卡片、团队介绍、客户案例展示，支持多语言切换和在线客服功能。',
  },
  {
    id: 'commerce',
    label: '在线商城',
    badge: '电商',
    prompt:
      '构建一个功能完整的在线商城，包含商品展示、购物车、用户注册登录、订单管理、支付结算等功能。设计现代化的商品卡片布局，支持商品搜索筛选、用户评价、优惠券系统和会员积分功能。',
  },
  {
    id: 'portfolio',
    label: '作品展示网站',
    badge: 'Lite',
    prompt:
      '制作一个精美的作品展示网站，适合设计师、摄影师、艺术家等创作者。包含作品画廊、项目详情页、个人简历、联系方式等模块。采用瀑布流或网格布局展示作品，支持图片放大预览和作品分类筛选。',
  },
]
const activeTemplateId = ref('')

// 设置提示词
const setPrompt = (prompt: string, templateId?: string) => {
  userPrompt.value = prompt
  if (templateId) {
    activeTemplateId.value = templateId
  }
}

const handleTemplateSelect = (preset: (typeof templatePresets)[number]) => {
  setPrompt(preset.prompt, preset.id)
}

const triggerButtonRipple = (event: MouseEvent) => {
  const target = event.currentTarget as HTMLElement | null
  if (!target) return
  target.classList.remove('is-rippling')
  const rect = target.getBoundingClientRect()
  const x = event.clientX - rect.left
  const y = event.clientY - rect.top
  target.style.setProperty('--ripple-x', `${x}px`)
  target.style.setProperty('--ripple-y', `${y}px`)
  void target.offsetWidth
  target.classList.add('is-rippling')
}

const handleCreateClick = (event: MouseEvent) => {
  triggerButtonRipple(event)
  void createApp()
}

// 创建应用
const createApp = async () => {
  if (!userPrompt.value.trim()) {
    message.warning('请输入应用描述')
    return
  }

  if (!loginUserStore.loginUser.id) {
    message.warning('请先登录')
    await router.push('/user/login')
    return
  }

  creating.value = true
  try {
    console.log('📌 创建应用 - 代码生成类型:', selectedCodeGenType.value)
    console.log('📌 创建应用 - 用户提示词:', userPrompt.value.trim())

    const res = await addApp({
      initPrompt: userPrompt.value.trim(),
      codeGenType: selectedCodeGenType.value,
    })

    if (res.data.code === 0 && res.data.data) {
      message.success('应用创建成功')
      // 跳转到对话页面，确保ID是字符串类型
      const appId = String(res.data.data)
      await router.push(`/app/chat/${appId}`)
    } else {
      message.error('创建失败：' + res.data.message)
    }
  } catch (error) {
    console.error('创建应用失败：', error)
    message.error('创建失败，请重试')
  } finally {
    creating.value = false
  }
}

// 加载我的应用
const loadMyApps = async () => {
  if (!loginUserStore.loginUser.id) {
    myApps.value = []
    myAppsPage.total = 0
    isMyAppsLoading.value = false
    return
  }

  isMyAppsLoading.value = true
  try {
    const res = await listMyAppVoByPage({
      pageNum: myAppsPage.current,
      pageSize: myAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      myApps.value = res.data.data.records || []
      myAppsPage.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    console.error('加载我的应用失败：', error)
  } finally {
    isMyAppsLoading.value = false
  }
}

// 加载精选应用
const loadFeaturedApps = async () => {
  isFeaturedLoading.value = true
  try {
    const res = await listGoodAppVoByPage({
      pageNum: featuredAppsPage.current,
      pageSize: featuredAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      featuredApps.value = res.data.data.records || []
      featuredAppsPage.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    console.error('加载精选应用失败：', error)
  } finally {
    isFeaturedLoading.value = false
  }
}

// 查看对话
const viewChat = (appId: string | number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}?view=1`)
  }
}

// 查看作品
const viewWork = (app: API.AppVO) => {
  if (app.deployKey) {
    const url = getDeployUrl(app.deployKey)
    window.open(url, '_blank')
  }
}

const skeletonPlaceholder = Array.from({ length: 3 }, (_, index) => index)

watch(
  () => loginUserStore.loginUser.id,
  (value) => {
    if (value) {
      void loadMyApps()
    } else {
      myApps.value = []
      myAppsPage.total = 0
    }
  },
)

watch(
  userPrompt,
  () => {
    isTypingPulse.value = true
    if (typingTimer) {
      window.clearTimeout(typingTimer)
    }
    typingTimer = window.setTimeout(() => {
      isTypingPulse.value = false
    }, 600)
  },
)

// 页面加载时获取数据
onMounted(() => {
  requestAnimationFrame(() => {
    pageReady.value = true
  })
  void loadMyApps()
  void loadFeaturedApps()
})

onBeforeUnmount(() => {
  if (typingTimer) {
    window.clearTimeout(typingTimer)
  }
})
</script>

<template>
  <div id="homePage" :class="{ 'page-ready': pageReady }">
    <div class="container">
      <!-- 网站标题和描述 -->
      <div class="hero-section" :class="{ 'is-visible': pageReady }">
        <div class="hero-chip">AI · NEXT</div>
        <h1 class="hero-title">AI 应用生成平台</h1>
        <p class="hero-description">一句话轻松创建网站应用</p>
      </div>

      <!-- 用户提示词输入框 -->
      <div class="input-section" :class="{ 'is-visible': pageReady }">
        <div class="input-card">
          <div class="input-card__glow" />

          <!-- 代码生成类型选择器 -->
          <div class="codegen-type-selector">
            <span class="selector-label">生成模式：</span>
            <a-radio-group v-model:value="selectedCodeGenType" button-style="solid" size="large">
              <a-radio-button
                v-for="option in CODE_GEN_TYPE_OPTIONS"
                :key="option.value"
                :value="option.value"
              >
                {{ option.label }}
              </a-radio-button>
            </a-radio-group>
          </div>

          <a-textarea
            v-model:value="userPrompt"
            placeholder="帮我创建个人博客网站"
            :rows="4"
            :maxlength="1000"
            class="prompt-input"
          />
          <div class="input-actions" :class="{ 'is-typing': isTypingPulse }">
            <span class="input-hint">Enter · 即刻生成</span>
            <a-button
              type="primary"
              size="large"
              class="launch-button"
              :loading="creating"
              @click="handleCreateClick"
            >
              <template #icon>
                <span class="launch-icon">⟶</span>
              </template>
              立即创建
            </a-button>
          </div>
        </div>
      </div>

      <!-- 快捷按钮 -->
      <div class="quick-actions">
        <button
          v-for="preset in templatePresets"
          :key="preset.id"
          type="button"
          class="template-card"
          :class="{ 'template-card--active': activeTemplateId === preset.id }"
          v-reveal
          @click="handleTemplateSelect(preset)"
        >
          <div class="template-card__head">
            <span class="template-badge">{{ preset.badge }}</span>
            <span class="template-status">推荐</span>
          </div>
          <strong class="template-title">{{ preset.label }}</strong>
          <p class="template-desc">智能生成 · 一键落地</p>
        </button>
      </div>

      <!-- 我的作品 -->
      <div class="section" id="myWorks">
        <div class="section-header">
          <h2 class="section-title">我的作品</h2>
          <p class="section-subtitle">构建专属 AI 应用 · 即刻部署到线上</p>
        </div>
        <div v-if="isMyAppsLoading" class="skeleton-grid">
          <div v-for="item in skeletonPlaceholder" :key="`my-skeleton-${item}`" class="card-skeleton">
            <div class="skeleton-image" />
            <div class="skeleton-lines">
              <span />
              <span />
            </div>
          </div>
        </div>
        <div v-else-if="myApps.length" class="app-grid fade-list">
          <AppCard
            v-for="app in myApps"
            :key="app.id"
            :app="app"
            v-reveal
            @view-chat="viewChat"
            @view-work="viewWork"
          />
        </div>
        <div v-else class="empty-state">
          <div class="empty-orb" />
          <p>还没有作品，试着输入你的第一个灵感吧。</p>
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="myAppsPage.current"
            v-model:page-size="myAppsPage.pageSize"
            :total="myAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个应用`"
            @change="loadMyApps"
          />
        </div>
      </div>

      <!-- 精选案例 -->
      <div class="section">
        <div class="section-header">
          <h2 class="section-title">精选案例</h2>
          <p class="section-subtitle">探索更多灵感，复用优秀的搭建模板</p>
        </div>
        <div v-if="isFeaturedLoading" class="skeleton-grid">
          <div v-for="item in skeletonPlaceholder" :key="`featured-skeleton-${item}`" class="card-skeleton">
            <div class="skeleton-image" />
            <div class="skeleton-lines">
              <span />
              <span />
            </div>
          </div>
        </div>
        <div v-else class="featured-grid fade-list">
          <AppCard
            v-for="app in featuredApps"
            :key="app.id"
            :app="app"
            :featured="true"
            v-reveal
            @view-chat="viewChat"
            @view-work="viewWork"
          />
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="featuredAppsPage.current"
            v-model:page-size="featuredAppsPage.pageSize"
            :total="featuredAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个案例`"
            @change="loadFeaturedApps"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
#homePage {
  width: 100%;
  min-height: 100vh;
  position: relative;
  margin: 0;
  padding: 0;
  background: transparent;
}

#homePage::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(138, 124, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(103, 219, 255, 0.05) 1px, transparent 1px);
  background-size:
    100px 100px,
    100px 100px;
  opacity: 0.3;
  z-index: 1;
  animation: gridFloat 25s ease-in-out infinite;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 120px 24px 80px;
  position: relative;
  z-index: 10;
  box-sizing: border-box;
}

.hero-section {
  text-align: center;
  padding: 60px 0;
  color: var(--text-primary);
  opacity: 0;
  transform: translateY(20px);
  transition: all var(--duration-slow) var(--ease-smooth);
  position: relative;
}

.hero-section::after {
  content: '';
  position: absolute;
  inset: -20px;
  background: radial-gradient(circle, var(--primary-color), transparent 60%);
  filter: blur(60px);
  z-index: -1;
}

.hero-chip {
  display: inline-flex;
  align-items: center;
  padding: 6px 16px;
  border-radius: var(--border-radius-full);
  font-size: 12px;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  background: var(--card-bg);
  border: 1px solid var(--primary-color);
  margin-bottom: 20px;
  color: var(--text-primary);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  box-shadow: var(--shadow-sm);
}

.hero-title {
  font-size: clamp(42px, 6vw, 72px);
  font-weight: 800;
  margin: 0 0 18px;
  line-height: 1.1;
  letter-spacing: -0.04em;
  background: linear-gradient(135deg, #ffffff 0%, #d87aee 50%, #ffffff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 0 25px rgba(216, 122, 238, 0.5));
  animation: titlePulse 6s ease-in-out infinite;
}

.hero-description {
  font-size: 20px;
  color: var(--text-secondary);
  margin: 0 auto;
  max-width: 560px;
  font-weight: 400;
  letter-spacing: 0.02em;
}

.hero-section.is-visible,
.input-section.is-visible {
  opacity: 1;
  transform: translateY(0);
}

.input-section {
  max-width: 860px;
  margin: 0 auto 48px;
  transition: all var(--duration-slow) var(--ease-smooth) 0.1s;
}

.input-card {
  position: relative;
  padding: 32px;
  border-radius: var(--border-radius-lg);
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.input-card__glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, var(--primary-color), transparent 50%);
  pointer-events: none;
}

.codegen-type-selector {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
  background: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--card-border);
}

.selector-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
}

.prompt-input {
  border-radius: var(--border-radius-md);
  border: 1px solid var(--card-border);
  font-size: 16px;
  padding: 22px;
  background: var(--bg-secondary);
  color: var(--text-primary);
  box-shadow: inset 0 0 0 1px var(--primary-color);
  transition: all var(--duration-normal) var(--ease-bounce);
}

.prompt-input::placeholder {
  color: var(--text-tertiary);
}

.prompt-input:focus {
  border-color: var(--primary-color);
  box-shadow:
    inset 0 0 0 1px var(--primary-color),
    var(--shadow-sm);
  transform: translateY(-2px);
}

.input-actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 18px;
  position: relative;
}

.input-actions::before {
  content: '';
  position: absolute;
  inset: -16px;
  border-radius: 24px;
  border: 1px solid rgba(138, 124, 255, 0.2);
  pointer-events: none;
}

.input-hint {
  font-size: 14px;
  color: var(--text-tertiary);
  letter-spacing: 0.08em;
}

.launch-button {
  border-radius: var(--border-radius-full);
  padding: 0 28px;
  height: 56px;
  font-size: 16px;
  font-weight: 600;
  background: var(--gradient-primary);
  border: none;
  color: var(--bg-primary);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
  transition: all var(--duration-normal) var(--ease-bounce);
}

.launch-button::after {
  content: '';
  position: absolute;
  width: 180px;
  height: 180px;
  background: rgba(255, 255, 255, 0.45);
  border-radius: 50%;
  transform: scale(0);
  opacity: 0;
  transition: transform 0.45s ease-out;
  pointer-events: none;
  top: calc(50% - 90px);
  left: calc(50% - 90px);
}

.launch-button.is-rippling::after {
  transform: scale(1);
  opacity: 0;
  transition: transform 0.75s ease-out, opacity 0.75s ease-out;
}

.launch-button:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-1px);
}

.launch-icon {
  display: inline-block;
  transform: rotate(-35deg);
  font-size: 20px;
  margin-right: 8px;
}

.input-actions.is-typing .launch-button {
  animation: pulseShadow 1.2s ease-in-out infinite;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 18px;
  margin-bottom: 72px;
}

.template-card {
  border: none;
  border-radius: var(--border-radius-md);
  padding: 20px;
  text-align: left;
  background: var(--card-bg);
  color: var(--text-primary);
  border: 2px solid transparent;
  background-clip: padding-box;
  position: relative;
  overflow: hidden;
  transition: all var(--duration-normal) var(--ease-bounce);
  box-shadow: var(--shadow-sm);
}

.template-card::before {
  content: '';
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

.template-card::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: var(--border-radius-md);
  background: var(--gradient-primary);
  opacity: 0;
  transition: opacity var(--duration-fast) var(--ease-smooth);
  z-index: 0;
}

.template-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-md);
}

.template-card:hover::before {
  opacity: 0.8;
}

.template-card:hover::after {
  opacity: 0.15;
}

.template-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  position: relative;
  z-index: 1;
}

.template-badge,
.template-status {
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.template-title {
  font-size: 18px;
  margin: 0 0 6px;
  color: var(--text-primary);
  position: relative;
  z-index: 1;
  font-weight: 700;
  background: var(--gradient-text);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.template-desc {
  margin: 0;
  font-size: 14px;
  color: var(--text-tertiary);
  position: relative;
  z-index: 1;
}

.template-card--active {
  border-color: var(--accent-color);
  box-shadow: var(--shadow-md);
}

.section {
  margin-bottom: 80px;
}

.section-header {
  margin-bottom: 32px;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0;
  color: var(--text-primary);
}

.section-subtitle {
  margin: 8px 0 0;
  color: var(--text-secondary);
  letter-spacing: 0.02em;
}

.app-grid,
.featured-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 28px;
  margin-bottom: 32px;
}

.fade-list > * {
  animation: fadeUp 0.6s ease forwards;
  opacity: 0;
}

.fade-list > *:nth-child(2) {
  animation-delay: 0.05s;
}

.fade-list > *:nth-child(3) {
  animation-delay: 0.1s;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.card-skeleton {
  border-radius: var(--border-radius-md);
  padding: 16px;
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  animation: shimmer 2s infinite;
}

.skeleton-image {
  height: 160px;
  border-radius: var(--border-radius-sm);
  background: var(--gradient-primary);
  margin-bottom: 18px;
  opacity: 0.3;
}

.skeleton-lines span {
  display: block;
  height: 12px;
  border-radius: var(--border-radius-full);
  background: var(--card-bg-hover);
  margin-bottom: 10px;
}

.skeleton-lines span:last-child {
  width: 60%;
  margin-bottom: 0;
}

.empty-state {
  padding: 60px 24px;
  text-align: center;
  color: var(--text-secondary);
  border: 1px dashed var(--card-border);
  border-radius: var(--border-radius-md);
  position: relative;
  overflow: hidden;
  background: var(--card-bg);
}

.empty-orb {
  width: 120px;
  height: 120px;
  margin: 0 auto 18px;
  border-radius: 50%;
  background: radial-gradient(circle, var(--primary-color), transparent 70%);
  animation: orbFloat 4s ease-in-out infinite;
}

@keyframes gridFloat {
  0% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(6px);
  }
  100% {
    transform: translateY(0);
  }
}

@keyframes titlePulse {
  0%,
  100% {
    filter: drop-shadow(0 0 25px rgba(138, 124, 255, 0.5));
  }
  50% {
    filter: drop-shadow(0 0 35px rgba(103, 219, 255, 0.6));
  }
}

@keyframes pulseShadow {
  0%,
  100% {
    box-shadow: 0 20px 35px rgba(138, 124, 255, 0.4);
  }
  50% {
    box-shadow: 0 25px 45px rgba(103, 219, 255, 0.5);
  }
}

@keyframes shimmer {
  0% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.6;
  }
}

@keyframes orbFloat {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .container {
    padding: 100px 16px 60px;
  }

  .input-card {
    padding: 24px;
  }

  .input-actions {
    flex-direction: column;
    align-items: flex-start;
  }

  .app-grid,
  .featured-grid {
    grid-template-columns: 1fr;
  }

  .template-card {
    padding: 16px;
  }
}
</style>
