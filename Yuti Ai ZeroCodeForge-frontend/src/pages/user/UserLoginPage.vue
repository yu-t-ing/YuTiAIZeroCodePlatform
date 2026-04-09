<template>
  <div id="userLoginPage">
    <h2 class="title">AI零代码应用生成 - 用户登录</h2>
    <div class="desc">不写一行代码，生成完整应用</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, message: '密码长度不能小于 8 位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>
      <div class="tips">
        没有账号
        <RouterLink to="/user/register">去注册</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">登录</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { userLogin } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const router = useRouter()
const loginUserStore = useLoginUserStore()

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const res = await userLogin(values)
  // 登录成功，把登录态保存到全局状态中
  if (res.data.code === 0 && res.data.data) {
    await loginUserStore.fetchLoginUser()
    message.success('登录成功')
    router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error('登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
#userLoginPage {
  /* 宇宙感紫色渐变背景 */
  background:
    radial-gradient(circle at 20% 30%, rgba(138, 124, 255, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(216, 122, 238, 0.12) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(85, 104, 211, 0.08) 0%, transparent 70%),
    radial-gradient(circle at 40% 80%, rgba(99, 58, 138, 0.1) 0%, transparent 60%),
    linear-gradient(180deg, rgba(10, 10, 20, 0.95) 0%, rgba(15, 15, 30, 0.98) 100%);
  max-width: 480px;
  padding: 40px;
  margin: 40px auto;
  border-radius: var(--border-radius-lg);
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(138, 124, 255, 0.2);
  box-shadow: var(--shadow-lg);
}

.title {
  text-align: center;
  margin-bottom: 16px;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #ffffff 0%, #d87aee 50%, #ffffff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 0 20px rgba(216, 122, 238, 0.5));
}

.desc {
  text-align: center;
  color: var(--text-secondary);
  margin-bottom: 32px;
  font-size: 16px;
}

.tips {
  text-align: right;
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 16px;
}

.tips a {
  color: var(--accent-color);
  text-decoration: none;
  font-weight: 500;
}

.tips a:hover {
  color: var(--primary-color);
  text-decoration: underline;
}

/* 表单样式优化 */
#userLoginPage :deep(.ant-form-item) {
  margin-bottom: 20px;
}

#userLoginPage :deep(.ant-input),
#userLoginPage :deep(.ant-input-password) {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(138, 124, 255, 0.3);
  color: var(--text-primary);
}

#userLoginPage :deep(.ant-input::placeholder),
#userLoginPage :deep(.ant-input-password input::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

#userLoginPage :deep(.ant-input:focus),
#userLoginPage :deep(.ant-input-password:focus) {
  background: rgba(255, 255, 255, 0.12);
  border-color: var(--primary-color);
  box-shadow: 0 0 10px rgba(138, 124, 255, 0.3);
}

#userLoginPage :deep(.ant-btn-primary) {
  background: var(--gradient-primary) !important;
  border: none !important;
  height: 44px;
  font-size: 16px;
  font-weight: 600;
}

#userLoginPage :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, #9d8eff, #d87aee) !important;
  box-shadow: 0 0 15px rgba(216, 122, 238, 0.5);
  transform: translateY(-2px);
}
</style>
