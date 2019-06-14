
// 路由器配置

import Vue from 'vue'
import Router from 'vue-router'

import About from '../views/About.vue'
import Experiments from '../views/Experiments.vue'
import Notes from '../views/Notes.vue'
import FileUpload from '../views/FileUpload.vue'
import HelloWorld from '../components/HelloWorld.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/about',
      component: About
    },
    {
      path: '/experiments',
      component: Experiments,
      children: [
        {
          path: 'marquee-led',
          component: FileUpload
        }
      ]
    },
    {
      path: '/notes',
      component: Notes
    },
    {
      path:'/login',
      component: HelloWorld
    },
    // {
    //   path: '/',
    //   redirect: '/about'
    // }
  ]
})
