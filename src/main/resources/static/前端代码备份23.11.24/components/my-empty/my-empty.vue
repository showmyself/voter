<template>
	<!-- <view  class="my-center-one" style="font-size: 120%;font-weight: bold;color:darkgray;height: 60rpx;">
		 --  绌 --
	</view> -->
	<view  class="my-center-one" style="margin-top: 20rpx;margin-bottom: 20rpx;">
			<u-empty :text="text" :mode="getIconModeByType"></u-empty>
		</view>
	</view>
</template>

<script>
	export default {
		name:"my-empty",
		props:{
			type:{//搜索列表search、收藏collect、评论comment、发帖post
				type:String,
				default() {
				    return "search"
				},
			},
			text:{
				type:String,
				default() {
				    return null
				},
			}
		},
		data() {
			return {
				iconType:""
			};
		},
		computed:{
			getIconModeByType(){
				if(this.type=="search")
					return "search";
				if(this.type=="collect")
					return "favor";
				if(this.type=="comment")
					return "message";
				if(this.type=="post")
					return "data";
				return "data";
			}
		}
	}
</script>

<style lang="scss" scoped>

</style>