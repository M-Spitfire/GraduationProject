<template>
  <div id="app">
    <HelloWorld v-if="isLogin"/>
    <Main v-else></Main>
    
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import Main from './components/Main.vue'
import PubSub from 'pubsub-js'
export default {
  data(){
    return{
      isLogin: true
    }
  },
  mounted(){
    PubSub.subscribe("login",(msg, data) => {
      this.isLogin = !this.isLogin;
    });
  },
  components: {
    HelloWorld,
    Main,
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  height: 100%;
  width:100%;
  height:100%;
  margin:0 auto;
}
html,body{
  width:100%;
  height:100%;
  margin:0;
  padding:0;
  overflow: hidden;
}
</style>
