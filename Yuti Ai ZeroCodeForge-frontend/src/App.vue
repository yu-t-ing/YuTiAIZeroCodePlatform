<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import CustomCursor from '@/components/CustomCursor.vue'
import ParticleBackground from '@/components/ParticleBackground.vue'
</script>

<template>
  <CustomCursor />
  <ParticleBackground />
  <BasicLayout />
</template>

<style>
/* 引入全局样式 */
@import '@/styles/global.css';
</style>
