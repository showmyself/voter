<template>
	<view class="my-page-root">
		<my-nav-bar title="登录-使用新密码"/>
		<view class="my-page-root-child form-page">

			<view style="height: 20rpx;"/>

			<view class="form-area">

				<view class="form-item">
					<text class="lable">用户名 或 邮箱</text>
					<input class="input" id="usernameOrEmail" v-model="formData.usernameOrEmail.value" type="text" @blur="blurValidate"/>
					<my-error-info :info="formData.usernameOrEmail.validate"/>
				</view>

				<view class="form-item">
					<text class="lable">密码</text>
					<input class="input" id="newPassword" v-model="formData.newPassword.value" type="password" @blur="blurValidate"/>
					<my-error-info :info="formData.newPassword.validate"/>
				</view>
				
				<view class="form-item">
					<view class="lable">验证码</view>
					<view style="float:left; width: 50%;">
						<input class="input" id="validCode" v-model="formData.validCode.value" type="text" @blur="blurValidate"/>
					</view>
					<view style="display: inline-block;margin-left: 10%;">
						<my-countdown showText="发送验证码" @start="getValidCode"/>
					</view>
					<my-error-info :info="formData.validCode.validate"/>
				</view>

				<view style="height: 60rpx;"/>

				<view style="float:right">
					<button class="confirm-button" @click="doLogin">确认登录</button>
				</view>

			</view>

		</view>
	</view>
</template>

<script>
import common from "@/pages/Self/_common.js"
const validator = common.validator

export default {
	data() {
		return {
			formData: {
				usernameOrEmail: {value:"",validate:""},
				newPassword: {value:"",validate:""},
				validCode: {value:"",validate:""},
			}
		};
	},
	methods: {
		blurValidate(el){
			const {id,value} = el.target
			this.formData[id].validate=this.validate(id,value)
		},
		validate(name,value){
			switch(name){
				case "usernameOrEmail":return validator.username(value); break;
				case "newPassword":return validator.password(value); break;
				case "validCode":return validator.securityCode(value); break;
			}
			return ""
		},
		doLogin(){
			if(!validator.validateAll(this.formData,this.validate)) return
			
			const dto={
				usernameOrEmail:this.formData.usernameOrEmail.value,
				newPassword:this.formData.newPassword.value,
				validCode:this.formData.validCode.value
			}
			
			this.api.user.forgetPassword(dto,(data)=>{
				const {user,token} = data
				this.vuex.set("user",user)
				this.vuex.set("isLogin",true)
				this.vuex.set("token",token)
				this.util.routeBack()
				this.util.routeBack()
				this.util.toast(user.username+" 已成功登录")
			})
		},
		getValidCode(){
			console.log("===getValidCode===")
		}
	}
};
</script>

<style lang="scss">

@import "@/common/my-class.scss";
@import "@/pages/Self/_common.scss";

</style>
