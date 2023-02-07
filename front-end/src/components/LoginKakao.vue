<template>
  <img
    class="kakao_btn"
    src="@/assets/images/kakaotalk_logo.png"
    @click="loginWithKakao"
    style="width: 50px; cursor: pointer"
  />
</template>

<script>
import { mapActions } from "vuex";
const memberStore = "memberStore";
export default {
  name: "LoginKakao",
  methods: {
    ...mapActions(memberStore, ["socialLogin"]),
    loginWithKakao() {
      window.Kakao.Auth.login({
        scope: "profile_nickname, account_email",
        success: this.getProfile,
      });
    },
    getProfile() {
      window.Kakao.API.request({
        url: "/v2/user/me",
        success: (res) => {
          const kakao_account = res.kakao_account;
          let emails = kakao_account.email;
          if (emails == null) {
            this.makeToast("이메일을 필수로 선택해주셔야 카카오로 로그인이 가능합니다.");
            if (this.$route.path != "/") this.$router.push({ name: "home" });
          } else {
            emails = emails.split("@");
            const req_body = {
              userId: "kakao_" + res.id,
              userName: kakao_account.profile.nickname,
              emailId: emails[0],
              emailDomain: emails[1],
              type: "kakao",
            };
            this.loginForKakao(req_body);
          }
        },
      });
    },
    loginForKakao(req_body) {
      this.socialLogin(req_body);
    },
    makeToast(msg) {
      this.$bvToast.toast(msg, {
        title: "알림",
        autoHideDelay: 1000,
        appendToast: true,
        variant: "warning",
      });
    },
  },
};
</script>
