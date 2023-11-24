<template>
	<view class="my-page-root">
		<my-nav-bar title="用户中心" :removeBack="true"/>
		<view class="my-page-root-child">
			
			<view style="height:10rpx"/>
			
			<view class="avatar-area">
				<view class="my-center-horizontal-one" style="margin-top:50rpx;">
					<view>
						<u-avatar :src="user.iconSrc"></u-avatar>
						
						<view class="my-center-horizontal-one">
							<view v-if="isLogin" class="username">{{user.username}}</view>
							<view v-else>
								<button class="login-button" @click="()=>{util.routeTo('Login')}">登录</button>
							</view>
						</view>
					</view>
				</view>
				
				<view style="margin-top:50rpx" v-if="isLogin">
					<!-- <text class="register-time">注册时间</text> -->
					<view style="	float: right;width: 15%;">
						<text class="mark">积分：{{user.mark}}</text>
					</view>
				</view>
			</view>
			
			<view style="height:15rpx"/>
			
			<view v-if="isLogin">
				<view class="menu">
					<a class="option" @click="()=>{util.routeTo('SelfDetails')}">查看个人信息</a>
				</view>
				
				<view class="menu-gap"/>
				
				<view class="menu">
					<image mode="widthFix" class="icon" :style="[st.icon()]" :src="require('@/static/统计图.png')"/>
					<a class="option" @click="()=>{util.routeTo('SelfPosts')}">已发帖</a>
				</view>
				
				<view class="menu-gap"/>
				
				<view class="menu">
					<image mode="widthFix" class="icon" :style="[st.icon()]" :src="require('@/static/收藏.png')"/>
					<a class="option" @click="()=>{util.routeTo('SelfCollects')}">已收藏</a>
				</view>
				
				<view class="menu-gap"/>
				
				<view class="menu">
					<image mode="widthFix" class="icon" :style="[st.icon()]" :src="require('@/static/评论.png')"/>
					<a class="option" @click="()=>{util.routeTo('SelfComments')}">已评论</a>
				</view>
				
				<view class="menu-gap"/>
				
				<view class="menu">
					<a class="option" @click="doLogout">退出登录</a>
				</view>
			</view>
			
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				user:{
					iconSrc:"",
					username:"?",
					sex:"",
					age:18
				},
				isLogin:false,
			}
		},
		methods: {
			doLogout(dontShowInfo){
				this.vuex.setDefault("user")
				this.vuex.setDefault("token")
				if(!dontShowInfo)
					this.util.toast(this.user.username+" 已退出登录")
				this.user={}
				this.isLogin=false
			},
			backRefresh(){
				this.isLogin=this.vuex.get("isLogin")
				this.user=this.vuex.get("user")
				if(!this.isLogin){
					this.doLogout(true)
				}
			}
		},
		onReady() {
			this.isLogin=this.vuex.get("isLogin")
			this.user=this.vuex.get("user")
		},
		onShow(){
			this.backRefresh()
		}
	}
</script>

<style lang="scss" scoped>
	
@import "@/common/my-class.scss";
	
.avatar-area{
	@extend .my-bfc;
	@extend .my-at-center-horizontal-block;
	width: 95%;
	height: 320rpx;
	margin-top: 20rpx;
	background-color: #ddd;
	border-radius: 10rpx;
	border-style: solid;
	border-color: skyblue;
	border-width: 10rpx;
	border-color: #999;
}
.login-button{
	font-size: 90%;
	font-weight: 600;
}
.username{
	color: cornflowerblue;
	font-size: 110%;
	font-weight: 600;
	height: 50rpx;
}
.register-time{
	font-size: 80%;
	margin-left: 3%;
	color: gray;
}
.menu{
	@extend .my-center-one;
	height: 60rpx;
	margin-top: 10rpx;
	margin-bottom: 10rpx;
	background-color: #eee;
}
.icon{
	margin-right: 2%;
}
.menu-gap{
	height: 3rpx;
}
.option{
	font-size: 110%;
	font-weight: 600;
	color: #666;
}

</style>
