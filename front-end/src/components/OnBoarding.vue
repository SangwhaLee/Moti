<template>
    <div>
        <carousel
              
     :per-page="1"
     :paginationPadding="2"
     :paginationActiveColor="'#04C584'"
     :paginationColor="'#dddddd'"
     
     paginationPosition="bottom"
     :paginationSize="7"
     :minSwipeDistance="200"
     >
            <slide class="single-slide">
                <div class="content">
                    <div class="title-box">
                        <p style="font-size:52px;">moti</p>
                    </div>
                    <br/>
                    <div>작지만 큰 실천을 도와주는 커뮤니티,<br/>모티에 오신 걸 환영해요.</div>
                        
                </div>
            </slide>
            <slide class="single-slide">
                <div class="content">
                    <img src="" alt="">
                    <div>가운데 미션수행 탭을 누르면<br/>수행할 성장 플랜을 선택할 수 있어요.</div>

                </div>
            </slide>
            <slide class="single-slide">
                <div class="content">
                    <img src="" alt="">
                    <div>하루에 한 번<br/>마음에 드는 미션 하나를 선택해서 수행하고<br/>후기를 작성하면 미션 성공이에요.</div>

                </div>
            </slide>
            <slide class="single-slide">
                <div class="content">
                    <img src="" alt="">
                    <div>일주일 중 5일 미션을 완수하면 성장플랜 완수에요. 당일 미션은 자정까지 수행 가능해요!</div>
                    <div><button class="btn-green" @click="go" style="flex-self:flex-end;">시작하기</button></div>

                </div>
            </slide>
        </carousel>
    </div>
</template>
<script>
import { Carousel, Slide } from 'vue-carousel'
export default {
    components: {
        Carousel,
        Slide,
    },
    methods:{
        go(){
            this.$router.push({ name: 'login' });
        }
    }
}
</script>
<style lang="scss">
   .single-slide{
    height:90vh;
    width:90vw;
   } 
   .content{
    height:90vh;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
   }
</style>