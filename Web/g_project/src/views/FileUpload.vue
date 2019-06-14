<template>
  <div>
    <el-row>
      <el-col :span="3">
        <label class="text-reader">
          上传文件
          <input type="file" @change="loadTextFromFile">  
        </label>
      </el-col>
      <el-col :span="12">
        <div v-html="showData" id="message"></div>
      </el-col>
    </el-row>
    
    <el-row id="lights">
      <el-col :span="1" :offset="6"><div class="grid-content bg-purple-light" id="0">0</div></el-col>
      <el-col :span="1"><div class="grid-content bg-purple-light" id="1">1</div></el-col>
      <el-col :span="1"><div class="grid-content bg-purple-light" id="2">2</div></el-col>
      <el-col :span="1"><div class="grid-content bg-purple-light" id="3">3</div></el-col>
      <el-col :span="1"><div class="grid-content bg-purple-light" id="4">4</div></el-col>
      <el-col :span="1"><div class="grid-content bg-purple-light" id="5">5</div></el-col>
      <el-col :span="1"><div class="grid-content bg-purple-light" id="6">6</div></el-col>
      <el-col :span="1"><div class="grid-content bg-purple-light" id="7">7</div></el-col>
    </el-row>

  </div>
  
</template>

<script>
export default {
  data(){
    return{
      //读取文件对象
      reader: null,
      //每次读取的文件大小字节数
      step: 1024,
      //当前已经读取的字节数
      cuLoaded: 0,
      //当前读取的文件对象
      file: null,
      //表示是否可以读取文件
      enableRead: true,
      //记录当前文件字节总数
      total: 0,
      //标识开始上传实践
      startTime: null,
      //websocket
      ws: null,   
      experimentData: null,   
      showData: 'lalala',
      record: 0,
    }
  },
  created(){
    this.createWebSocket();
  },
  destroyed(){
    this.ws.close();
  },
  methods: {
    //读取文件并上传
    loadTextFromFile(e) {
      this.file = e.target.files[0];
      this.total = this.file.size;
      console.log("file size:" + this.file.size);
      if(this.ws == null){
        if(window.confirm("连接服务器失败，是否重连")){
          this.createWebSocket(() => {
            this.bindReader();
          });
        }
        return;
      }
      this.bindReader();
    },

    //创建websocket
    createWebSocket(){
      const url = "ws://192.168.43.42:8080/WEB/websocket"
      this.ws = new WebSocket(url);
      this.ws.onopen = function(e){
        console.log("连接成功！");
        // if(onSuccess){
        //   onSuccess();
        // }
      }
      this.ws.onmessage = (e) => {
        document.getElementById('message').innerHTML = e.data;
        //this.showData += e.data + '<br/>';
        let data = e.data;
        const completeData = ["127", "191", "223", "239", "247", "251", "253", "254"];
        //console.log(typeof(data) + ':' + data);
        //alert(typeof(completeData[0]));
        this.experimentData = data;
        let index = completeData.indexOf(data);
        //console.log("第" + index + "个");
        // let lights = document.getElementById("lights");
        // let children = lights.childNodes;
        //this.lights = document.getElementsByClassName("grid-content");
        if(index < 0){
          //console.log("data error")
        }
        else{
          let id = String(index);
          let light = document.getElementById(id);
          light.setAttribute("class", "grid-content bg-purple-dark");
          let temp = this.record;
          id = String(temp);
          //console.log("typrof oldLight:" + typeof(temp) + ' ' + temp);
          //console.log("typrof id:" + typeof(id) + ' ' + id);
          let oldLight = document.getElementById(id);
          oldLight.setAttribute("class", "grid-content bg-purple-light");
          this.record = index;
        }
      }
      this.ws.onclose = function(e){
        stop();
        console.log("连接关闭");
      }
      this.ws.onerror = function(e){
        stop();
        console.log(e);
        console.log("ERROR!!!!!!!!!!");
      }
    },

    //绑定reader,分段读取
    bindReader(){
      this.cuLoaded = 0;
      this.startTime = new Date();
      this.enableRead = true;
      this.reader = new FileReader();
      this.reader.onload = (e) => {
        console.log("读取总数：" + e.loaded);
        if(this.enableRead == false){
          return false;
        }
        if(this.cuLoaded >= this.total){
          setTimeout(function(){
            //继续读取
            console.log("--------->进入等待");
            this.loadSuccess(e.loaded);
          },3);
        }
        else{
          this.loadSuccess(e.loaded);
        }
      }
      this.readBlob();
    },

    //读取文件成功
    loadSuccess(loaded){
      //将分段数据上传到服务器
      let blob = this.reader.result;
      //使用websocket向服务器发送数据
      if(this.cuLoaded == 0){
        this.ws.send(this.file.name);
      }
      this.ws.send(blob);
      //读取剩余部分
      this.cuLoaded += loaded;
      if(this.cuLoaded < this.total){
        this.readBlob();
      }
      else{
        this.ws.send("END");
        console.log("总共上传：" + this.cuLoaded + "总共用时：" + (new Date().getTime() - this.startTime.getTime()) / 1000);
      }
    },

    //从指定位置读取文件
    readBlob(){
      //指定开始i位置和结束位置，读取文件
      let blob = this.file.slice(this.cuLoaded, this.cuLoaded + this.step);
      this.reader.readAsArrayBuffer(blob);
    }
  }
};
</script>

<style>
.text-reader {
  position: relative;
  overflow: hidden;
  display: inline-block;

  /* Fancy button looking */
  border: 1px solid black;
  border-radius: 5px;
  padding: 4px 6px;
  cursor: pointer;
}
.text-reader input {
  position: absolute;
  top: 0;
  left: 0;
  z-index: -1;
  opacity: 0;
}
#message{
  display: inline;
  line-height: 45px;
}
.el-row {
  margin-bottom: 20px;
  
}
.el-col {
  border-radius: 4px;
  padding: 5px
}
.bg-purple-dark {
  background: #99a9bf;
}
.bg-purple {
  background: #d3dce6;
}
.bg-purple-light {
  background: #e5e9f2;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
  text-align: center;
  line-height: 36px;
  
}
.row-bg {
  padding: 10px 0;
  background-color: #f9fafc;
}
</style>