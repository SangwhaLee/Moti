<template>
  <div class="account-form-comp">
    <on-boarding></on-boarding>
  </div>
</template>

<script>
import OnBoarding from '@/components/OnBoarding'
export default {
	name: 'LoginView',
  components:{
    OnBoarding
  },
  methods: {
    go() {
      this.$router.push({ path: '/authcomplete' })
    }
  },
  // created() {
  //   // 이미 로그인되어 있을 경우 피드로 이동
  //   if (this.$store.getters.isLoggedIn) {
  //     this.$router.push({ name: 'feed' })
  //   }
  // }
}
</script>

<style lang="scss">

</style>