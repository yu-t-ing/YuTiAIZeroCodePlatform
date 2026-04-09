<template>
  <div id="userManagePage">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider />
    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag class="role-tag-admin">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag class="role-tag-user">普通用户</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button danger @click="doDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUser, listUserVoByPage } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 展示的数据
const data = ref<API.UserVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  const res = await listUserVoByPage({
    ...searchParams,
  })
  if (res.data.data) {
    data.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格分页变化时的操作
const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索数据
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 删除数据
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteUser({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } else {
    message.error('删除失败')
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
#userManagePage {
  padding: 24px;
  /* 宇宙感紫蓝渐变背景 */
  background:
    radial-gradient(circle at 20% 30%, rgba(138, 124, 255, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(103, 219, 255, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(138, 124, 255, 0.08) 0%, transparent 70%),
    linear-gradient(180deg, rgba(10, 10, 20, 0.95) 0%, rgba(15, 15, 30, 0.98) 100%);
  margin-top: 16px;
  border-radius: var(--border-radius-md);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

/* 表格容器样式增强 */
#userManagePage :deep(.ant-table) {
  background: rgba(255, 255, 255, 0.03);
  border-radius: var(--border-radius-md);
}

#userManagePage :deep(.ant-table-thead > tr > th) {
  background: rgba(138, 124, 255, 0.1);
  color: var(--text-primary);
  border-bottom: 1px solid rgba(138, 124, 255, 0.2);
}

#userManagePage :deep(.ant-table-tbody > tr > td) {
  background: rgba(255, 255, 255, 0.02);
  color: var(--text-secondary);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

#userManagePage :deep(.ant-table-tbody > tr:hover > td) {
  background: rgba(138, 124, 255, 0.08);
}

/* 表单样式优化 */
#userManagePage :deep(.ant-form) {
  background: rgba(255, 255, 255, 0.03);
  padding: 20px;
  border-radius: var(--border-radius-md);
  margin-bottom: 20px;
}

/* 表单标签颜色 */
#userManagePage :deep(.ant-form-item-label > label) {
  color: #ffffff !important;
}

#userManagePage :deep(.ant-input) {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(138, 124, 255, 0.3);
  color: #ffffff !important;
}

#userManagePage :deep(.ant-input::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

#userManagePage :deep(.ant-input:focus) {
  background: rgba(255, 255, 255, 0.08);
  border-color: var(--primary-color);
  box-shadow: 0 0 10px rgba(138, 124, 255, 0.2);
  color: #ffffff !important;
}

#userManagePage :deep(.ant-divider) {
  border-color: rgba(138, 124, 255, 0.2);
}

/* 按钮样式 */
#userManagePage :deep(.ant-btn-primary) {
  background: var(--gradient-primary) !important;
  border: none !important;
  color: #ffffff !important;
}

#userManagePage :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, #9d8eff, #77e5ff) !important;
  box-shadow: 0 0 15px rgba(138, 124, 255, 0.4);
}

#userManagePage :deep(.ant-btn-dangerous) {
  background: rgba(255, 77, 79, 0.8) !important;
  border: none !important;
  color: #ffffff !important;
}

#userManagePage :deep(.ant-btn-dangerous:hover) {
  background: rgba(255, 77, 79, 1) !important;
  box-shadow: 0 0 15px rgba(255, 77, 79, 0.4);
}

/* 标签样式 */
#userManagePage :deep(.ant-tag) {
  color: #ffffff !important;
}

/* 用户角色标签样式 */
#userManagePage :deep(.role-tag-admin) {
  background: linear-gradient(135deg, #52c41a, #73d13d) !important;
  border: 1px solid #52c41a !important;
  color: #ffffff !important;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 4px;
}

#userManagePage :deep(.role-tag-user) {
  background: linear-gradient(135deg, #8a7cff, #67dbff) !important;
  border: 1px solid #8a7cff !important;
  color: #ffffff !important;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 4px;
}
</style>
