<template>
  <div id="note">
    <el-scrollbar>
      <el-input
        type="textarea"
        :rows="rows"
        autofocus="true"
        v-model="textarea"
        @keydown.9.native.prevent="handleClickTab">
      </el-input>
    </el-scrollbar>
  </div>
</template>

<script>
export default {
  data(){
    return{
      textarea: '',
      rows: 1,
    }
  },
  mounted: function(){
    //设置文本框行数
    let div = document.getElementById("note");
    this.rows = (div.clientHeight - 12) / 21 + 1;

    //从local storage中读取保存的文本
    this.textarea = JSON.parse(localStorage.getItem("textArea") || '');
  },
  methods:{
    handleClickTab(){
      this.textarea += "    ";
    }
  },
  watch:{
    textarea:{
      deep: true,
      handler: function(newValue){
       localStorage.setItem("textArea", JSON.stringify(newValue));
      }
    }
  }
}
</script>

<style>
  #note{
    width: 100%;
    height: 100%;
  }
</style>
