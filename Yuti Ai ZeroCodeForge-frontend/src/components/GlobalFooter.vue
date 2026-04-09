<template>
  <a-layout-footer class="footer">
    <div class="footer-content">
      <p class="copyright">
        <a
          href="https://www.codefather.cn"
          target="_blank"
          rel="noopener noreferrer"
          class="author-link"
        >
          原创项目 by 姜瓷
        </a>
      </p>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">
// 无需额外的响应式数据
</script>

<style scoped>
.footer {
  background: rgba(10, 10, 20, 0.6);
  backdrop-filter: blur(10px);
  text-align: center;
  padding: 20px;
  margin-top: 40px;
  border-top: 1px solid rgba(138, 124, 255, 0.2);
}

.copyright {
  margin: 0;
  color: var(--text-secondary);
  font-size: 14px;
}

.author-link {
  color: var(--accent-color);
  text-decoration: none;
  transition: all var(--duration-normal) var(--ease-bounce);
}

.author-link:hover {
  color: var(--primary-color);
  text-shadow: 0 0 10px var(--primary-color);
}
</style>
