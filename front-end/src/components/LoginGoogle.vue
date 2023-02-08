<template>
  <img
    class="google_btn"
    src="@/assets/images/google_logo.png"
    @click="loginWithGoogle"
    style="width: 50px; cursor: pointer"
  />
</template>

<script>
import { mapActions } from "vuex";
export default {
  name: "LoginGoogle",
  methods: {
    ...mapActions(["socialLogin"]),
    async loginWithGoogle() {
      let oauth2Endpoint = "https://accounts.google.com/o/oauth2/v2/auth";
      let form = document.createElement('form');
      form.setAttribute('method','GET');
      form.setAttribute('action',oauth2Endpoint);

      let params = {
        "client_id":"293618779849-da4im7oh35dq851ro2l9htrvi4ivbtc0.apps.googleusercontent.com",
        "redirect_uri":"http://localhost:9000/auth/google",
        "response_type":"code",
        "scope":"email profile",
      };

      for(var p in params){
        let input = document.createElement('input');
        input.setAttribute('type','hidden');
        input.setAttribute('name',p);
        input.setAttribute('value',params[p]);
        form.appendChild(input);
      }
      
      document.body.appendChild(form);

      form.submit();

},
    getProfile() {
    },
    loginForGoogle(req_body) {
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
