<template>
  <a-layout-header
    :class="[
      'header',
      {
        'header--scrolled': isScrolled
      }
    ]"
  >
    <div class="header-glow" />
    <a-row :wrap="false" align="middle" class="header-row">
      <!-- 左侧：Logo和标题 -->
      <a-col flex="0 0 auto">
        <RouterLink to="/">
          <div class="header-left">
            <div class="logo-wrapper">
              <img class="logo" src="@/assets/logo.png" alt="Logo" />
            </div>
            <h1 class="site-title">姜瓷 AI</h1>
          </div>
        </RouterLink>
      </a-col>
      <!-- 中间：导航菜单 -->
      <a-col flex="1 1 auto">
        <div class="nav-wrapper">
          <a-menu
            v-model:selectedKeys="selectedKeys"
            mode="horizontal"
            :items="menuItems"
            @click="handleMenuClick"
          />
        </div>
      </a-col>
      <!-- 右侧：用户操作区域 -->
      <a-col flex="0 0 auto">
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <div class="user-entry user-entry--active">
                <div class="entry-avatar">
                  <a-avatar :src="loginUserStore.loginUser.userAvatar" :size="42">
                    {{ loginUserStore.loginUser.userName?.charAt(0) || '姜' }}
                  </a-avatar>
                  <span class="entry-pulse" />
                </div>
                <div class="entry-meta">
                  <span class="entry-label">欢迎回来</span>
                  <span class="entry-name">{{ loginUserStore.loginUser.userName ?? '无名' }}</span>
                </div>
              </div>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="goToProfile">
                    <UserOutlined />
                    个人中心
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <button class="user-entry" type="button" @click="handleUserEntry">
              <div class="entry-avatar">
                <div class="avatar-fallback">✨</div>
                <span class="entry-pulse" />
              </div>
              <div class="entry-meta">
                <span class="entry-name">登录</span>
              </div>
            </button>
          </div>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogout } from '@/api/userController.ts'
import { LogoutOutlined, HomeOutlined, UserOutlined } from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()
// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
const isScrolled = ref(false)

// 监听路由变化，更新当前选中菜单
router.afterEach((to) => {
  selectedKeys.value = [to.path]
})

const handleScroll = () => {
  const currentScrollY = window.scrollY
  isScrolled.value = currentScrollY > 10
}

onMounted(() => {
  handleScroll()
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/appManage',
    label: '应用管理',
    title: '应用管理',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://individualjourney.netlify.app/', target: '_blank' }, 'Individual_Journal'),
    title: 'IndividualJourney',
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 退出登录
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}

// 跳转到个人中心
const goToProfile = () => {
  router.push('/user/profile')
}

const handleUserEntry = () => {
  router.push('/user/login')
}
</script>

<style scoped>
/* 强制应用样式到 a-layout-header */
.header {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  z-index: 1000 !important;
  padding: 16px 48px;
  background: var(--card-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--card-border);
  transition: all var(--duration-normal) var(--ease-smooth);
  width: 100% !important;
  height: auto !important;
}

.header--scrolled {
  background: rgba(10, 10, 10, 0.85);
  box-shadow: var(--shadow-md);
  padding: 12px 48px;
}

.header-glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 20% 10%, var(--primary-color), transparent 70%);
  pointer-events: none;
  opacity: 0.15;
}

.header-row {
  position: relative;
  z-index: 2;
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
}

.header-row .ant-col {
  display: flex;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform var(--duration-normal) var(--ease-bounce);
}

.header-left:hover {
  transform: scale(1.02);
}

.logo-wrapper {
  position: relative;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-wrapper::before {
  content: '';
  position: absolute;
  inset: -4px;
  background: var(--gradient-primary);
  border-radius: 14px;
  opacity: 0.5;
  filter: blur(12px);
  z-index: -1;
  animation: logoGlow 3s ease-in-out infinite;
}

.logo {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  box-shadow: var(--shadow-sm);
  transition: transform var(--duration-normal) var(--ease-bounce),
    box-shadow var(--duration-normal) var(--ease-smooth);
}

.logo:hover {
  transform: rotate(5deg) scale(1.05);
  box-shadow: var(--shadow-md);
}

.site-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  background: var(--gradient-text);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  background-size: 200% 200%;
  animation: gradientShift 5s ease infinite;
  letter-spacing: -0.5px;
}

.ant-menu-horizontal {
  border-bottom: none !important;
  background: transparent;
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-left: auto;
}

.ant-menu-item {
  color: var(--text-secondary) !important;
  font-size: 16px;
  font-weight: 500;
  padding-inline: 18px !important;
  border-radius: var(--border-radius-sm);
  transition: all var(--duration-normal) var(--ease-bounce);
  position: relative;
  overflow: hidden;
}

.ant-menu-item::before {
  content: '';
  position: absolute;
  inset: 0;
  background: var(--gradient-primary);
  opacity: 0;
  transition: opacity var(--duration-fast) var(--ease-smooth);
}

.ant-menu-item :deep(.ant-menu-title-content) {
  color: var(--text-secondary) !important;
  position: relative;
  z-index: 1;
}

.ant-menu-item :deep(span),
.ant-menu-item :deep(a) {
  color: var(--text-secondary) !important;
}

.ant-menu-item::after {
  display: none;
}

.ant-menu-item:hover::before {
  opacity: 0.15;
}

.ant-menu-item:hover {
  color: var(--text-primary) !important;
  transform: translateY(-1px);
}

.ant-menu-item:hover :deep(.ant-menu-title-content),
.ant-menu-item:hover :deep(span),
.ant-menu-item:hover :deep(a) {
  color: var(--text-primary) !important;
}

.ant-menu-item-selected {
  background: var(--gradient-primary) !important;
  color: var(--text-primary) !important;
}

.ant-menu-item-selected::before {
  opacity: 1;
}

.ant-menu-item-selected :deep(.ant-menu-title-content),
.ant-menu-item-selected :deep(span),
.ant-menu-item-selected :deep(a) {
  color: var(--text-primary) !important;
}

.user-login-status {
  display: flex;
  justify-content: flex-end;
}

.user-entry {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 18px;
  border-radius: var(--border-radius-md);
  border: 1.5px solid var(--card-border);
  background: var(--card-bg);
  color: var(--text-primary);
  cursor: pointer;
  transition: all var(--duration-normal) var(--ease-bounce);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.user-entry:hover {
  transform: translateY(-2px);
  border-color: var(--primary-color);
  background: var(--card-bg-hover);
  box-shadow: var(--shadow-sm);
}

.user-entry--active {
  background: var(--bg-tertiary);
  border-color: var(--primary-color);
}

.entry-avatar {
  position: relative;
  display: grid;
  place-items: center;
}

.entry-avatar :deep(.ant-avatar) {
  border: 2.5px solid var(--primary-color);
  background: var(--bg-tertiary);
  color: var(--text-primary);
  font-weight: 600;
  box-shadow: 0 0 15px rgba(138, 124, 255, 0.3);
}

.avatar-fallback {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-primary);
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  box-shadow: var(--shadow-sm);
}

.entry-pulse {
  position: absolute;
  width: 54px;
  height: 54px;
  border-radius: 50%;
  border: 1px solid var(--primary-color);
  animation: pulse 2.5s infinite;
  pointer-events: none;
}

.entry-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
  gap: 2px;
}

.entry-label {
  font-size: 11px;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: var(--accent-color);
  margin-bottom: 0;
  font-weight: 500;
}

.entry-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

/* 动画效果 */
@keyframes logoGlow {
  0%,
  100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

@keyframes gradientShift {
  0%,
  100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

@keyframes pulse {
  0% {
    opacity: 0.8;
    transform: scale(0.85);
  }
  100% {
    opacity: 0;
    transform: scale(1.3);
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .header {
    padding: 14px 32px;
  }

  .header--scrolled {
    padding: 10px 32px;
  }

  .ant-menu-item {
    padding-inline: 16px !important;
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .header {
    padding: 12px 20px;
  }

  .header--scrolled {
    padding: 8px 20px;
  }

  .site-title {
    font-size: 18px;
  }

  .ant-menu-item {
    padding-inline: 12px !important;
    font-size: 13px;
  }

  .user-entry {
    padding: 8px 14px;
    gap: 10px;
  }

  .entry-avatar :deep(.ant-avatar) {
    width: 36px !important;
    height: 36px !important;
    font-size: 16px !important;
  }

  .avatar-fallback {
    width: 36px;
    height: 36px;
    font-size: 18px;
  }

  .entry-pulse {
    width: 48px;
    height: 48px;
  }

  .entry-label {
    font-size: 10px;
  }

  .entry-name {
    font-size: 14px;
  }

  .logo,
  .logo-wrapper {
    width: 40px;
    height: 40px;
  }
}
</style>

<style>
/* 强制覆盖 Ant Design 菜单文字颜色 */
.header .ant-menu-item,
.header .ant-menu-item .ant-menu-title-content,
.header .ant-menu-item span,
.header .ant-menu-item a {
  color: var(--text-secondary) !important;
}

.header .ant-menu-item:hover,
.header .ant-menu-item:hover .ant-menu-title-content,
.header .ant-menu-item:hover span,
.header .ant-menu-item:hover a {
  color: var(--text-primary) !important;
}

.header .ant-menu-item-selected,
.header .ant-menu-item-selected .ant-menu-title-content,
.header .ant-menu-item-selected span,
.header .ant-menu-item-selected a {
  color: var(--text-primary) !important;
}

/* 修复菜单溢出按钮（三个点）的颜色 */
.header .ant-menu-overflow-item,
.header .ant-menu-overflow-item-rest {
  color: var(--text-secondary) !important;
}

.header .ant-menu-overflow-item .anticon,
.header .ant-menu-overflow-item-rest .anticon {
  color: var(--text-secondary) !important;
}

.header .ant-menu-submenu-title,
.header .ant-menu-submenu-arrow {
  color: var(--text-secondary) !important;
}

/* 下拉菜单样式 */
.header .ant-dropdown-menu {
  background: var(--bg-secondary) !important;
  border: 1px solid var(--card-border) !important;
  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);
}

.header .ant-dropdown-menu-item {
  color: var(--text-secondary) !important;
}

.header .ant-dropdown-menu-item:hover {
  background: var(--card-bg-hover) !important;
  color: var(--text-primary) !important;
}

.header .anticon-more {
  color: var(--text-secondary) !important;
}

.header .ant-menu-submenu-expand-icon,
.header .ant-menu-submenu-arrow {
  color: var(--text-secondary) !important;
}

/* 强制覆盖 Ant Design Layout Header 的默认样式 */
.ant-layout-header.header {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  width: 100% !important;
  height: auto !important;
  margin: 0 !important;
  padding: 16px 48px;
  line-height: normal !important;
}
</style>
