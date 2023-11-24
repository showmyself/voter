<template>
	<view>
		<!-- 自定义导航栏封装 -->
		<u-navbar 
		:title="title" :title-size="28" title-color="white" 
		:is-back="!removeBack" :back-icon-size="25" back-icon-color="white" :custom-back="routeBack"
		:background="background">
			<!--自定义失败，H5布局正确，但小程序端始终不正确，只有放弃了-->
			<!-- <view class="slot-wrap">
				<view style="width: 20%">
					<image mode="aspectFit" style="width: 20rpx" :src="require('@/components/my-nav-bar/左箭头.png')"/>
				</view>
				<view class="my-center-horizontal-one" style="width: 60%; color:white">
					{{title}}
				</view>
				<view style="width: 20%">
				</view>
			</view> -->
		</u-navbar>
	</view>
</template>

<script>
	export default {
		name:"my-nav-bar",
		props:["title","removeBack"],
		data(){
			return {
				background:{
					backgroundColor:'gray'
				}
			}
		},
		methods:{
			routeBack(){
				this.util.routeBack()
			}
		}
	}
</script>

<style lang="scss" scoped>
.slot-wrap {
	display: flex;
	align-items: center;
	/* 让slot内容占满整个导航栏的宽度 */
	flex: 1;
	/* 让slot内容与导航栏左右有空隙 */
	padding: 0 30rpx;
}
</style>