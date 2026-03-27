<template>
  <!-- 顶部导航栏 -->
  <div class="app">
    <Head/>
    <router-view/>
    <Footer v-if="!$route.meta.hideFooter"/>
  </div>
</template>

<script setup>
import Footer from './pages/Footer.vue';
import Head from './pages/Head.vue';
</script>

