<script setup>
import { RouterLink, RouterView } from 'vue-router'
import HeaderNav from './components/HeaderNav.vue'
</script>

<template>
  <div id="app">
    <HeaderNav />
    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  padding: 20px;
}
</style>
