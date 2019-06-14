<template>
  <div class="login_style">
    <div class="block"></div>
    <h2 class="title">系统登陆</h2>
    <el-form ref="form" :model="form" label-width="80px">
      <el-form-item label="username">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="password">
        
        <el-input v-model="form.password" type="password"></el-input>
      </el-form-item>
      <el-form-item style="align: center">
        <el-button type="primary" @click="onSubmit">登录</el-button>
        <el-button @click="register">注册</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import PubSub from 'pubsub-js'
export default {
  data() {
    return {
      form: {
        name: '',
        password: '',
      },

      ws: null
    }
  },
  created(){
    this.createWebSocket();
  },
  methods: {
    onSubmit() {
      if(this.form.name == ''||this.form.password == ''){
        this.$message('用户名或密码不能为空');
      }
      else{
        //this.ws.send("CHECKNAMEANDPASSWORD");
        let info = "login" + '%' + this.form.name + '%' + this.form.password;
        //console.log(info);
        this.ws.send(info);
        console.log('submit!');
      }
    },
    register(){
      if(this.form.name == ''||this.form.password == ''){
        this.$message('用户名或密码不能为空');
      }
      else{
        let info = "register" + '%' + this.form.name + '%' + this.form.password;
        //console.log(info);
        this.ws.send(info);
      }
    },
    createWebSocket(){
      const url = "ws://192.168.43.42:8080/WEB/login";
      this.ws = new WebSocket(url);
      this.ws.onopen = function(e){
        console.log("连接db！");
      }
      this.ws.onmessage = (e) => {
        let data = e.data;
        console.log("判断结果：" + data);
        if(data == "true"){
          //提醒登录成功
          console.log("登录成功");
          PubSub.publish("login", this.form.name);
          this.$message("登陆成功");
          this.$router.push({path:'/experiments'});
          stop();
        }
        else if(data == "false"){
          console.log("用户名或密码错误");
          this.$message("用户名或密码错误");
        }
        else{
          this.$message(data);
        }
      }
      this.ws.onclose = function(e){
        stop();
        console.log("登录完成");
      }
      this.ws.onerror = function(e){
        stop();
        console.log(e);
        console.log("ERROR!!!!!!!!!!");
      }
    },
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.login_style{
  margin-left: auto;
  margin-right: auto;
  width: 25%
}
.block{
  height: 150px;
}
.title{
  margin-left: 80px;
  color: #545c64
}
</style>
