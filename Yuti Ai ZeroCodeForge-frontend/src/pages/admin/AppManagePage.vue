<template>
  <div id="appManagePage">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="应用名称">
        <a-input v-model:value="searchParams.appName" placeholder="输入应用名称" />
      </a-form-item>
      <a-form-item label="创建者">
        <a-input v-model:value="searchParams.userId" placeholder="输入用户ID" />
      </a-form-item>
      <a-form-item label="生成类型">
        <a-select
          v-model:value="searchParams.codeGenType"
          placeholder="选择生成类型"
          style="width: 150px"
        >
          <a-select-option value="">全部</a-select-option>
          <a-select-option
            v-for="option in CODE_GEN_TYPE_OPTIONS"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
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
      :scroll="{ x: 1200 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'cover'">
          <a-image v-if="record.cover" :src="record.cover" :width="80" :height="60" />
          <div v-else class="no-cover">无封面</div>
        </template>
        <template v-else-if="column.dataIndex === 'initPrompt'">
          <a-tooltip :title="record.initPrompt">
            <div class="prompt-text">{{ record.initPrompt }}</div>
          </a-tooltip>
        </template>
        <template v-else-if="column.dataIndex === 'codeGenType'">
          {{ formatCodeGenType(record.codeGenType) }}
        </template>
        <template v-else-if="column.dataIndex === 'priority'">
          <a-tag v-if="record.priority === 99" class="priority-tag-featured">精选</a-tag>
          <span v-else class="priority-number">{{ record.priority || 0 }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'deployedTime'">
          <span v-if="record.deployedTime">
            {{ formatTime(record.deployedTime) }}
          </span>
          <span v-else class="text-gray">未部署</span>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ formatTime(record.createTime) }}
        </template>
        <template v-else-if="column.dataIndex === 'user'">
          <UserInfo :user="record.user" size="small" />
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="primary" size="small" @click="editApp(record)"> 编辑 </a-button>
            <a-button
              type="default"
              size="small"
              @click="toggleFeatured(record)"
              :class="{ 'featured-btn': record.priority === 99 }"
            >
              {{ record.priority === 99 ? '取消精选' : '精选' }}
            </a-button>
            <a-popconfirm title="确定要删除这个应用吗？" @confirm="deleteApp(record.id)">
              <a-button danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { listAppVoByPageByAdmin, deleteAppByAdmin, updateAppByAdmin } from '@/api/appController'
import { CODE_GEN_TYPE_OPTIONS, formatCodeGenType } from '@/utils/codeGenTypes'
import { formatTime } from '@/utils/time'
import UserInfo from '@/components/UserInfo.vue'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
    fixed: 'left',
  },
  {
    title: '应用名称',
    dataIndex: 'appName',
    width: 150,
  },
  {
    title: '封面',
    dataIndex: 'cover',
    width: 100,
  },
  {
    title: '初始提示词',
    dataIndex: 'initPrompt',
    width: 200,
  },
  {
    title: '生成类型',
    dataIndex: 'codeGenType',
    width: 100,
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    width: 80,
  },
  {
    title: '部署时间',
    dataIndex: 'deployedTime',
    width: 160,
  },
  {
    title: '创建者',
    dataIndex: 'user',
    width: 120,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 160,
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right',
  },
]

// 数据
const data = ref<API.AppVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.AppQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAppVoByPageByAdmin({
      ...searchParams,
    })
    if (res.data.data) {
      data.value = res.data.data.records ?? []
      total.value = res.data.data.totalRow ?? 0
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败：', error)
    message.error('获取数据失败')
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

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

// 表格变化处理
const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 编辑应用
const editApp = (app: API.AppVO) => {
  router.push(`/app/edit/${app.id}`)
}

// 切换精选状态
const toggleFeatured = async (app: API.AppVO) => {
  if (!app.id) return

  const newPriority = app.priority === 99 ? 0 : 99

  try {
    const res = await updateAppByAdmin({
      id: app.id,
      priority: newPriority,
    })

    if (res.data.code === 0) {
      message.success(newPriority === 99 ? '已设为精选' : '已取消精选')
      // 刷新数据
      fetchData()
    } else {
      message.error('操作失败：' + res.data.message)
    }
  } catch (error) {
    console.error('操作失败：', error)
    message.error('操作失败')
  }
}

// 删除应用
const deleteApp = async (id: number | undefined) => {
  if (!id) return

  try {
    const res = await deleteAppByAdmin({ id })
    if (res.data.code === 0) {
      message.success('删除成功')
      // 刷新数据
      fetchData()
    } else {
      message.error('删除失败：' + res.data.message)
    }
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}
</script>

<style scoped>
#appManagePage {
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
#appManagePage :deep(.ant-table) {
  background: rgba(255, 255, 255, 0.03);
  border-radius: var(--border-radius-md);
}

#appManagePage :deep(.ant-table-thead > tr > th) {
  background: rgba(138, 124, 255, 0.1);
  color: var(--text-primary);
  border-bottom: 1px solid rgba(138, 124, 255, 0.2);
}

#appManagePage :deep(.ant-table-tbody > tr > td) {
  background: rgba(255, 255, 255, 0.02);
  color: var(--text-secondary);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

#appManagePage :deep(.ant-table-tbody > tr:hover > td) {
  background: rgba(138, 124, 255, 0.08);
}

/* 表单样式优化 */
#appManagePage :deep(.ant-form) {
  background: rgba(255, 255, 255, 0.03);
  padding: 20px;
  border-radius: var(--border-radius-md);
  margin-bottom: 20px;
}

/* 表单标签颜色 */
#appManagePage :deep(.ant-form-item-label > label) {
  color: #ffffff !important;
}

#appManagePage :deep(.ant-input),
#appManagePage :deep(.ant-select-selector) {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(138, 124, 255, 0.3);
  color: #ffffff !important;
}

#appManagePage :deep(.ant-input::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

#appManagePage :deep(.ant-input:focus),
#appManagePage :deep(.ant-select-focused .ant-select-selector) {
  background: rgba(255, 255, 255, 0.08);
  border-color: var(--primary-color);
  box-shadow: 0 0 10px rgba(138, 124, 255, 0.2);
  color: #ffffff !important;
}

#appManagePage :deep(.ant-select-selection-placeholder) {
  color: rgba(255, 255, 255, 0.5) !important;
}

#appManagePage :deep(.ant-select-single .ant-select-selector .ant-select-selection-item) {
  color: #ffffff !important;
}

#appManagePage :deep(.ant-divider) {
  border-color: rgba(138, 124, 255, 0.2);
}

/* 按钮样式 */
#appManagePage :deep(.ant-btn-primary) {
  background: var(--gradient-primary) !important;
  border: none !important;
  color: #ffffff !important;
}

#appManagePage :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, #9d8eff, #77e5ff) !important;
  box-shadow: 0 0 15px rgba(138, 124, 255, 0.4);
}

#appManagePage :deep(.ant-btn-dangerous) {
  background: rgba(255, 77, 79, 0.8) !important;
  border: none !important;
  color: #ffffff !important;
}

#appManagePage :deep(.ant-btn-dangerous:hover) {
  background: rgba(255, 77, 79, 1) !important;
  box-shadow: 0 0 15px rgba(255, 77, 79, 0.4);
}

/* 标签样式 */
#appManagePage :deep(.ant-tag) {
  color: #ffffff !important;
}

/* 优先级标签样式 */
#appManagePage :deep(.priority-tag-featured) {
  background: linear-gradient(135deg, #faad14, #ffc53d) !important;
  border: 1px solid #faad14 !important;
  color: #ffffff !important;
  font-weight: 700;
  padding: 4px 12px;
  border-radius: 4px;
  box-shadow: 0 0 10px rgba(250, 173, 20, 0.4);
}

#appManagePage :deep(.priority-tag-featured:hover) {
  background: linear-gradient(135deg, #ffc53d, #ffd666) !important;
  box-shadow: 0 0 15px rgba(250, 173, 20, 0.6);
}

#appManagePage .priority-number {
  color: var(--text-secondary);
  font-weight: 500;
}

.no-cover {
  width: 80px;
  height: 60px;
  background: rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
  border-radius: 4px;
  border: 1px solid rgba(138, 124, 255, 0.2);
}

.prompt-text {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--text-secondary);
}

.text-gray {
  color: rgba(255, 255, 255, 0.6);
}

.featured-btn {
  background: #faad14;
  border-color: #faad14;
  color: white;
}

.featured-btn:hover {
  background: #d48806;
  border-color: #d48806;
}

:deep(.ant-table-tbody > tr > td) {
  vertical-align: middle;
}
</style>
