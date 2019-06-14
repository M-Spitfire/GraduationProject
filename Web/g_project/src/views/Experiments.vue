<template>
  <el-container style="height: 600px">
    <el-scrollbar style="100%">
      <el-aside width="200px" style="background-color: rgb(238, 241, 246)">
        <el-menu :default-openeds="['1']" active-text-color="#000000">
          <el-submenu index="1" >
            <template slot="title"><i class="el-icon-odometer"></i>实验</template>
            
            <el-menu-item index="1-1">
              <router-link to="/experiments/marquee-led">
                <a>流水灯</a>
              </router-link>
            </el-menu-item>
            
            <el-menu-item index="1-2">实验2</el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>
    </el-scrollbar>

    <el-main style="padding: 5px" v-loading= 'loading'>
      <keep-alive>
        <router-view></router-view>
      </keep-alive>
    </el-main>
  </el-container>
</template>

<script>
export default {
  data() {
    const item = {
      date: '2016-05-02',
      name: '王小虎',
      address: '上海市普陀区金沙江路 1518 弄'
    };
    return {
      tableData: Array(0).fill(item),
      loading: false,
      activeIndex: '1',
    }
  }
}
</script>

<style>
  .el-scrollbar__wrap {
    overflow-x: hidden;
  }

  .el-aside {
    color: #333;
  }
  a:visited{
    color: #000000
  }
</style>
