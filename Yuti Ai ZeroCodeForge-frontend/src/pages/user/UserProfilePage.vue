<template>
  <div id="userProfilePage">
    <div class="profile-container">
      <!-- 左侧：个人信息卡片 -->
      <div class="profile-card">
        <div class="card-header">
          <h2>个人中心</h2>
        </div>

        <!-- 头像区域 -->
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <a-avatar :size="120" :src="formData.userAvatar">
              {{ formData.userName?.charAt(0) || 'U' }}
            </a-avatar>
          </div>
          <div class="avatar-actions">
            <a-button type="link" @click="showAvatarModal = true">
              <template #icon>
                <EditOutlined />
              </template>
              修改头像
            </a-button>
          </div>
        </div>

        <!-- 基本信息 -->
        <a-divider>基本信息</a-divider>
        <a-form
          :model="formData"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 18 }"
          layout="horizontal"
        >
          <a-form-item label="账号">
            <a-input v-model:value="currentUser.userAccount" disabled />
          </a-form-item>

          <a-form-item label="昵称">
            <a-input
              v-model:value="formData.userName"
              placeholder="请输入昵称"
              :maxlength="20"
              show-count
            />
          </a-form-item>

          <a-form-item label="个人简介">
            <a-textarea
              v-model:value="formData.userProfile"
              placeholder="介绍一下自己吧..."
              :rows="4"
              :maxlength="200"
              show-count
            />
          </a-form-item>

          <a-form-item label="角色">
            <a-tag :color="currentUser.userRole === 'admin' ? 'red' : 'blue'">
              {{ currentUser.userRole === 'admin' ? '管理员' : '普通用户' }}
            </a-tag>
          </a-form-item>

          <a-form-item label="创建时间">
            <span class="form-value">{{ formatTime(currentUser.createTime) }}</span>
          </a-form-item>
        </a-form>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <a-space>
            <a-button type="primary" @click="handleUpdate" :loading="loading">
              <template #icon>
                <SaveOutlined />
              </template>
              保存修改
            </a-button>
            <a-button @click="handleReset">
              <template #icon>
                <ReloadOutlined />
              </template>
              重置
            </a-button>
          </a-space>
        </div>
      </div>

      <!-- 右侧：统计信息 -->
      <div class="stats-section">
        <div class="stat-card">
          <div class="stat-icon">
            <AppstoreOutlined />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.appCount }}</div>
            <div class="stat-label">我的应用</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <MessageOutlined />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.chatCount }}</div>
            <div class="stat-label">对话次数</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 头像修改弹窗 -->
    <a-modal
      v-model:open="showAvatarModal"
      title="修改头像"
      :footer="null"
      width="500px"
    >
      <a-form layout="vertical">
        <a-form-item label="上传头像">
          <a-upload
            :before-upload="handleBeforeUpload"
            :show-upload-list="false"
            accept="image/png,image/jpeg,image/jpg,image/gif"
          >
            <a-button type="primary" block>
              <template #icon>
                <UploadOutlined />
              </template>
              选择图片
            </a-button>
          </a-upload>
          <div class="avatar-tip">
            💡 提示：支持 PNG、JPG、JPEG、GIF 格式，建议尺寸 200x200 像素
          </div>
        </a-form-item>

        <a-form-item label="预览">
          <div class="avatar-preview">
            <a-avatar :size="100" :src="tempAvatarUrl">
              {{ formData.userName?.charAt(0) || 'U' }}
            </a-avatar>
          </div>
        </a-form-item>

        <a-form-item :wrapper-col="{ span: 24 }">
          <a-space style="width: 100%; justify-content: flex-end">
            <a-button @click="handleCancelAvatar">取消</a-button>
            <a-button type="primary" @click="handleUpdateAvatar" :disabled="!tempAvatarUrl">确定</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import {
  EditOutlined,
  SaveOutlined,
  ReloadOutlined,
  AppstoreOutlined,
  MessageOutlined,
  UploadOutlined,
} from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { updateMyUser } from '@/api/userController'
import dayjs from 'dayjs'

const loginUserStore = useLoginUserStore()
const currentUser = computed(() => loginUserStore.loginUser)

// 表单数据
const formData = ref<API.UserUpdateRequest>({
  id: currentUser.value.id,
  userName: currentUser.value.userName || '',
  userAvatar: currentUser.value.userAvatar || '',
  userProfile: currentUser.value.userProfile || '',
  userRole: undefined, // 不允许修改角色
})

// 原始数据（用于重置）
const originalData = ref<API.UserUpdateRequest>({ ...formData.value })

// 加载状态
const loading = ref(false)

// 头像弹窗
const showAvatarModal = ref(false)
const tempAvatarUrl = ref('')

// 统计数据
const stats = ref({
  appCount: 0,
  chatCount: 0,
})

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 更新用户信息
const handleUpdate = async () => {
  // 校验
  if (!formData.value.userName || formData.value.userName.trim() === '') {
    message.error('昵称不能为空')
    return
  }

  if (formData.value.userName.trim().length < 2) {
    message.error('昵称至少需要2个字符')
    return
  }

  try {
    loading.value = true
    const res = await updateMyUser(formData.value)

    if (res.data.code === 0) {
      message.success('更新成功')

      // 更新store中的用户信息
      await loginUserStore.fetchLoginUser()

      // 更新原始数据
      originalData.value = { ...formData.value }
    } else {
      message.error('更新失败：' + res.data.message)
    }
  } catch (error) {
    console.error('更新失败：', error)
    message.error('更新失败，请重试')
  } finally {
    loading.value = false
  }
}

// 重置表单
const handleReset = () => {
  formData.value = { ...originalData.value }
  message.info('已重置')
}

// 打开头像弹窗
const handleOpenAvatarModal = () => {
  tempAvatarUrl.value = formData.value.userAvatar || ''
  showAvatarModal.value = true
}

// 文件上传前的处理
const handleBeforeUpload = (file: File) => {
  // 检查文件类型
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件！')
    return false
  }

  // 检查文件大小（限制为2MB）
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB！')
    return false
  }

  // 使用FileReader读取文件为base64
  const reader = new FileReader()
  reader.onload = (e) => {
    tempAvatarUrl.value = e.target?.result as string
    message.success('图片已选择，请点击"确定"保存')
  }
  reader.onerror = () => {
    message.error('图片读取失败，请重试')
  }
  reader.readAsDataURL(file)

  return false // 阻止自动上传
}

// 取消修改头像
const handleCancelAvatar = () => {
  tempAvatarUrl.value = formData.value.userAvatar || ''
  showAvatarModal.value = false
}

// 更新头像
const handleUpdateAvatar = () => {
  if (!tempAvatarUrl.value) {
    message.error('请先选择图片')
    return
  }

  formData.value.userAvatar = tempAvatarUrl.value
  showAvatarModal.value = false
  message.success('头像已修改，记得点击"保存修改"按钮保存哦！')
}

// 页面加载时获取统计数据
onMounted(() => {
  // TODO: 调用后端接口获取统计数据
  // 这里先使用默认值，等后端接口实现后再调用
  stats.value = {
    appCount: 0,
    chatCount: 0,
  }
})
</script>

<style scoped>
#userProfilePage {
  min-height: 100vh;
  padding: 24px;
  background:
    radial-gradient(circle at 20% 30%, rgba(138, 124, 255, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(216, 122, 238, 0.12) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(85, 104, 211, 0.08) 0%, transparent 70%),
    linear-gradient(180deg, rgba(10, 10, 20, 0.95) 0%, rgba(15, 15, 30, 0.98) 100%);
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
}

.profile-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(138, 124, 255, 0.2);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.card-header {
  margin-bottom: 32px;
}

.card-header h2 {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #ffffff 0%, #d87aee 50%, #ffffff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 0 20px rgba(216, 122, 238, 0.5));
  margin: 0;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32px;
}

.avatar-wrapper {
  margin-bottom: 16px;
}

.avatar-wrapper :deep(.ant-avatar) {
  border: 4px solid rgba(138, 124, 255, 0.3);
  background: var(--gradient-primary);
  color: #ffffff;
  font-size: 48px;
  font-weight: 700;
}

.avatar-actions :deep(.ant-btn-link) {
  color: var(--accent-color);
  font-weight: 500;
}

.avatar-actions :deep(.ant-btn-link:hover) {
  color: var(--primary-color);
}

:deep(.ant-divider) {
  color: var(--text-secondary);
  font-size: 16px;
  font-weight: 600;
}

:deep(.ant-form-item-label) > label {
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
}

/* 修复表单项外框样式 */
:deep(.ant-form-item) {
  background: transparent;
}

:deep(.ant-form-item-control-input) {
  background: transparent;
}

:deep(.ant-form-item-control-input-content) {
  background: transparent;
}

/* 表单值样式 */
.form-value {
  color: #ffffff;
  font-weight: 500;
}

:deep(.ant-input),
:deep(.ant-input-textarea textarea) {
  background: rgba(15, 15, 30, 0.8) !important;
  border-color: rgba(138, 124, 255, 0.3);
  color: rgba(255, 255, 255, 0.95);
}

:deep(.ant-input::placeholder),
:deep(.ant-input-textarea textarea::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

:deep(.ant-input:disabled) {
  background: rgba(15, 15, 30, 0.6) !important;
  color: rgba(255, 255, 255, 0.6) !important;
}

:deep(.ant-input:focus),
:deep(.ant-input-textarea textarea:focus) {
  background: rgba(30, 30, 50, 0.8) !important;
  border-color: var(--primary-color);
  box-shadow: 0 0 10px rgba(138, 124, 255, 0.3);
  color: #ffffff;
}

.form-actions {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid rgba(138, 124, 255, 0.2);
  display: flex;
  justify-content: center;
}

.stats-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(138, 124, 255, 0.2);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(138, 124, 255, 0.3);
  border-color: rgba(138, 124, 255, 0.4);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #ffffff;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 4px;
}

/* 弹窗样式 */
.avatar-tip {
  margin-top: 8px;
  padding: 12px;
  background: rgba(138, 124, 255, 0.1);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  line-height: 1.5;
}

.avatar-preview {
  display: flex;
  justify-content: center;
  padding: 16px;
  background: rgba(138, 124, 255, 0.05);
  border-radius: 8px;
}

.avatar-preview :deep(.ant-avatar) {
  border: 3px solid rgba(138, 124, 255, 0.3);
  background: var(--gradient-primary);
  color: #ffffff;
  font-size: 40px;
  font-weight: 700;
}

/* 弹窗中的输入框样式 */
:deep(.ant-modal-content) .ant-input {
  background: rgba(15, 15, 30, 0.8) !important;
  border-color: rgba(138, 124, 255, 0.3);
  color: rgba(255, 255, 255, 0.95);
}

:deep(.ant-modal-content) .ant-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

:deep(.ant-modal-content) .ant-input:focus {
  background: rgba(30, 30, 50, 0.8) !important;
  border-color: var(--primary-color);
  color: #ffffff;
}

/* 弹窗整体样式 */
:deep(.ant-modal-content) {
  background: rgba(15, 15, 30, 0.95) !important;
  border: 1px solid rgba(138, 124, 255, 0.3);
}

/* 弹窗标题 */
:deep(.ant-modal-header) {
  background: transparent !important;
  border-bottom: 1px solid rgba(138, 124, 255, 0.2);
}

:deep(.ant-modal-title) {
  color: #ffffff !important;
  font-weight: 600;
}

/* 弹窗内容区域 */
:deep(.ant-modal-body) {
  background: transparent !important;
  color: rgba(255, 255, 255, 0.85);
}

/* 弹窗中的表单标签 */
:deep(.ant-modal-content) .ant-form-item-label > label {
  color: rgba(255, 255, 255, 0.9) !important;
  font-weight: 500;
}

/* 弹窗中的提示文字 */
:deep(.ant-modal-content) .avatar-tip {
  color: rgba(255, 255, 255, 0.8) !important;
  background: rgba(138, 124, 255, 0.1);
  border-radius: 8px;
  padding: 12px;
  margin-top: 8px;
}

/* 弹窗中的所有按钮文字 */
:deep(.ant-modal-content) .ant-btn {
  color: rgba(255, 255, 255, 0.95) !important;
}

:deep(.ant-modal-content) .ant-btn-default {
  background: rgba(255, 255, 255, 0.05) !important;
  border-color: rgba(138, 124, 255, 0.3) !important;
  color: rgba(255, 255, 255, 0.9) !important;
}

:deep(.ant-modal-content) .ant-btn-default:hover {
  background: rgba(138, 124, 255, 0.2) !important;
  border-color: rgba(138, 124, 255, 0.5) !important;
  color: #ffffff !important;
}

/* 上传按钮样式 */
:deep(.ant-upload-wrapper) {
  width: 100%;
}

:deep(.ant-btn-primary) {
  background: var(--gradient-primary) !important;
  border-color: rgba(138, 124, 255, 0.5) !important;
  color: #ffffff !important;
  font-weight: 500;
}

:deep(.ant-btn-primary:hover) {
  background: var(--primary-color) !important;
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 20px rgba(138, 124, 255, 0.5);
}

:deep(.ant-btn:disabled) {
  background: rgba(255, 255, 255, 0.1) !important;
  border-color: rgba(255, 255, 255, 0.2) !important;
  color: rgba(255, 255, 255, 0.4) !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    grid-template-columns: 1fr;
  }

  .stats-section {
    flex-direction: row;
  }

  .stat-card {
    flex: 1;
  }
}
</style>
