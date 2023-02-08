<template>
    <div></div>
</template>
<script>
import { mapActions } from "vuex";
export default {
  name: "AuthGoogle",
  mounted(){
    this.getProfile();
  },
  methods: {
    ...mapActions(["socialLogin"]),
    getProfile() {
        
      fetch("https://www.googleapis.com/oauth2/v3/userinfo",{
        headers:{

        }
      })

    },
    loginForGoogle(req_body) {
      this.socialLogin(req_body);
    },

  },
};
</script>
