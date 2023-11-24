
import Vue from 'vue'
Vue.config.productionTip = false

import App from './App'
App.mpType = 'app'

import uView from 'uview-ui'
Vue.use(uView);//执行后，Vue.prototype.$u被赋值了一些东西
//Vue.use保证对同一个插件仅执行一次（后续use同一个插件将无效），无论第二个参数代入何

//下面一般会把相关数据、函数赋值到Vue.prototype中，另外需要保证顺序正确
import constant from "@/common/my-constant.js"
Vue.use(constant)

import util from "@/common/my-util.js"
Vue.use(util)

import api from "@/common/my-api.js"
Vue.use(api)

import style from "@/common/my-style.js"
Vue.use(style)

import vuex from "@/common/my-vuex.js"
Vue.use(vuex)

import http from "@/common/my-http.js"
Vue.use(http)

import i18n from "@/common/i18n/my-i18n.js"
Vue.use(i18n)

import my_mixin from "@/common/my-mixin.js"
Vue.mixin(my_mixin)

const app = new Vue({...App,store:Vue.prototype.store})

app.$mount()
