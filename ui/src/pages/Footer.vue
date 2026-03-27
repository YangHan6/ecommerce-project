<template>
  <footer class="footer">
    <div class="footer-wrapper">
      <div class="footer-left">
        <div class="logo">◆</div>
        <p class="title">练习商城</p>
        <p class="sub">个人学习项目</p>
      </div>

      <div class="footer-right">
        <p class="section-title">联系方式</p>
        <a href="tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=2099704595" class="link">
          QQ：2099704595
        </a>
        <a href="mailto:yang1535028@gmail.com" class="link">
          Email：yang1535028@gmail.com
        </a>
      </div>

      <div class="footer-middle">
        <p class="section-title">导航</p>
        <a href="javascript:;" class="link">关于我们</a>
        <a href="javascript:;" class="link">联系我们</a>
      </div>
    </div>

    <div class="footer-bottom">
      <span>© 2025 练习商城 · 仅供学习使用</span>
    </div>
  </footer>
</template>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.footer {
  background: #fff;
  border-top: 1px solid #ebebeb;
  font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.footer-wrapper {
  display: flex;
  justify-content: center;
  gap: 80px;
  padding: 48px 40px;
}

.footer-left .logo {
  font-size: 16px;
  color: #111;
  margin-bottom: 12px;
  display: inline-block;
  animation: spin 12s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
}

.footer-left .title {
  font-size: 14px;
  font-weight: 600;
  color: #111;
  margin-bottom: 4px;
}

.footer-left .sub {
  font-size: 12px;
  color: #bbb;
}

.section-title {
  font-size: 11px;
  font-weight: 600;
  color: #bbb;
  letter-spacing: 1px;
  text-transform: uppercase;
  margin-bottom: 14px;
}

.footer-right,
.footer-middle {
  display: flex;
  flex-direction: column;
}

.link {
  font-size: 13px;
  color: #888;
  text-decoration: none;
  margin-bottom: 10px;
  transition: color 0.2s ease;
}

.link:last-child {
  margin-bottom: 0;
}

.link:hover {
  color: #111;
}

.footer-bottom {
  border-top: 1px solid #f0f0f0;
  padding: 16px 40px;
  text-align: center;
  font-size: 12px;
  color: #ccc;
}
</style>