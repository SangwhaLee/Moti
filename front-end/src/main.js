import Vue from 'vue'
import App from './App.vue'
import store from './store'
import router from './router'
import lineClamp from 'vue-line-clamp'
import axios from 'axios'
import VModal from 'vue-js-modal'
//import GAuth from 'vue-google-oauth2'

import NavigationBar from './components/NavigationBar.vue'


Vue.component(NavigationBar.name, NavigationBar)

Vue.config.productionTip = false

Vue.use(lineClamp,{
  importCss: true,
})

Vue.use(VModal, {dialog: true})

Vue.prototype.$axios = axios
store.$axios = axios

// const baseUrl = 'http://i8a509.p.ssafy.io:8080'
//const baseUrl = 'https://moti.today/api'
const baseUrl = 'http://localhost:8080/api'
Vue.prototype.$baseUrl = baseUrl
store.$baseUrl = baseUrl
window.Kakao.init("332addc510b9cc23dc54269380c84107");

// const gauthOption = {
//   clientId: '293618779849-da4im7oh35dq851ro2l9htrvi4ivbtc0.apps.googleusercontent.com',
//   scope: 'profile email',
//   prompt: 'select_account'
// }
// Vue.use(GAuth, gauthOption)
// store에서 router 사용하기
store.$router = router

new Vue({
  store,
  router,
  axios,
  render: h => h(App)
}).$mount('#app')