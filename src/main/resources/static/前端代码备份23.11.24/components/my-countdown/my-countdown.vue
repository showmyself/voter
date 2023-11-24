<template>
	<view>
		<!-- u-button推荐使用:custom-style来做样式 -->
		<u-button :custom-style="buttonStyle" @click="clickButton" size="mini" :disabled="count>0">
			{{count>0?count:showText}}
		</u-button>
	</view>
</template>

<script>
	export default {
		name:"my-countdown",
		props:["showText"],
		data() {
			return {
			    count: 0,//当前剩余秒数
			    timer: null,//暂存计时器
				buttonStyle:{
					
				}
			}
		},
		created(){
			this.count=this.vuex.get("timeCount")//用vuex记忆count
			if(this.count>0)
				this.beginCountdown()
		},
		methods:{
			clickButton(){
				this.$emit("start")
				this.count=30
				this.beginCountdown()
			},
			beginCountdown(){
				this.timer = setInterval(() => {
					if (this.count > 0) {
						this.count--
						this.vuex.set("timeCount",this.count)
					}
					else
						clearInterval(this.timer);
				}, 1000)
			}
		}
	}
</script>

<style lang="scss">

</style>