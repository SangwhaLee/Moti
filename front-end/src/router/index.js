import Vue from 'vue'
import VueRouter from 'vue-router'
import SignupView from '@/views/SignupView'
import LoginView from '@/views/LoginView'
import MyMissionView from '@/views/MyMissionView'
import SignupForm from '@/components/SignupForm'
import SignupAuth from '@/components/SignupAuth'
import SignupNickname from '@/components/SignupNickname'
import FeedView from '@/views/FeedView'
import ProfileMain from '@/views/ProfileView'
import SearchPage from '@/views/SearchView'
import UserFlowerList from '@/views/UserFLList'
import LoginForm from '@/components/LoginForm'
import FeedPage from '@/views/FeedPage'
import ProfileMain from '@/views/ProfileMain'
import SearchPage from '@/views/SearchPage'
import UserFlowerList from '@/views/UserFlowerList'
import MyMissionMain from '@/components/MyMissionMain'
import FeedComment from '@/components/FeedComment'

Vue.use(VueRouter)

const routes = [
  {
    path: '/signup',
    name: 'signup',
    component: SignupView,
    redirect: '/signup/main',
    children: [
      {
        path: 'main',
        component: SignupForm
      },
      {
        path: 'auth',
        component: SignupAuth
      },
      {
        path: 'nickname',
        component: SignupNickname
      },
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    redirect: '/login/main',
    children: [
      {
        path: 'main',
        component: LoginForm
      },
    ],
  },
  {
    path:'/feed',
    name:'feed',
    component: FeedView,
  },
  {
    path:'/comment/:feedId',
    name:'comment',
    component:FeedComment,
  },
  {
    path:'/profile',
    name:'profile',
    component: ProfileMain,
  },
  {
    path:'/search',
    name:'search',
    component:SearchPage,
  },
  {
    path:'/userflowerlist',
    name:'userflowerlist',
    component:UserFlowerList,
  },
  {
    path: '/my-mission',
    name: 'my-mission',
    component: MyMissionView,
    redirect: '/my-mission/main',
    children: [
      {
        path: 'main',
        component: MyMissionMain,
      },
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
