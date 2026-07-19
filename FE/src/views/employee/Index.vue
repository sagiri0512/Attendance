<template>
  <div class="employee-page">
    <h2 class="page-title">员工管理</h2>
    <router-view />
  </div>
</template>

<style scoped>
.employee-page {
  max-width: 1100px;
  margin: 0 auto;
}

.page-title {
  margin: 0 0 24px;
  font-size: 22px;
  font-weight: 600;
}
</style>
