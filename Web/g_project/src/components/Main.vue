<template>
<el-container style="height: 100%; border: 0px solid #eee">
  <el-header style="text-align: right; font-size: 14px; padding: 0px">
      
    <el-menu 
    :default-active="activeIndex"
    class="el-menu-demo"
    mode="horizontal"
    background-color="#545c64"
    text-color="#fff"
    active-text-color="#ffd04b"
    style="padding-left: 0px; padding-right: 20px">
      <el-menu-item index="1">
        <router-link to="/about">
          <a>关于</a>
        </router-link>
      </el-menu-item>
      <el-menu-item index="2">
        <router-link to="/experiments">
          <a>实验</a>
        </router-link>
      </el-menu-item>
      <el-menu-item index="3">
        <router-link to="/notes">
          <a>笔记</a>
        </router-link>
      </el-menu-item>
      
      <el-dropdown>
        <i class="el-icon-setting" style="margin-right: 15px"></i>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item>查看</el-dropdown-item>
          <el-dropdown-item>新增</el-dropdown-item>
          <el-dropdown-item>删除</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <span v-html="welcome"></span>
    </el-menu>
  </el-header>
  

  <keep-alive>
    <router-view></router-view>
  </keep-alive>
</el-container>
</template>

<script>
import PubSub from 'pubsub-js'
export default {
  data(){
    return {
      activeIndex: '1',
      welcome: "欢迎使用！"
    }
  },
  mounted(){
    PubSub.subscribe("login",(msg, data) => {
      console.log(data);
      this.welcome += data;
    });
  },
}
</script>

<style>
.el-header {
  color: #333;
  line-height: 60px;
  padding: 0px;
  color: #fff;
}
.router-link{
  text-decoration: none;
} 
a{
  text-decoration: none;
}
</style>
