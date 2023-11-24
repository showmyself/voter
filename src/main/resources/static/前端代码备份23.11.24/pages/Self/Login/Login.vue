<template>
	<view class="my-page-root">
		<my-nav-bar title="登录"/>
		<view class="my-page-root-child form-page">

			<view style="height: 20rpx;"/>

			<view class="form-area">

				<view class="form-item">
					<text class="lable">用户名 或 邮箱</text>
					<!-- id对于验证有用 -->
					<input class="input" id="usernameOrEmail" v-model="formData.usernameOrEmail.value" type="text" @blur="blurValidate"/>
					<my-error-info :info="formData.usernameOrEmail.validate"/>
				</view>

				<view class="form-item">
					<text class="lable">密码</text>
					<input class="input" id="password" v-model="formData.password.value" type="password" @blur="blurValidate"/>
					<my-error-info :info="formData.password.validate"/>
				</view>

				<view class="form-item">
					<view class="lable">验证码</view>
					<view style="display: inline-block; width: 60%;">
						<input class="input" id="captcha" v-model="formData.captcha.value" type="text" @blur="blurValidate"/>
					</view>
					<view style="float:right;">
						<image class="captcha" mode="heightFix" :src="captchaUrl"/>
					</view>
					<my-error-info :info="formData.captcha.validate"/>
				</view>

				<view style="height: 60rpx;"/>
				
				<view style="float:right">
					<button class="confirm-button" @click="doLogin">确认登录</button>
				</view>
				
				<view style="float:left;">
					<a class="link" @click="toRegisterPage">前往注册</a>
					<view style="height:10rpx"/>
					<a class="link" @click="toForgetPwdPage">忘记密码</a>
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
			//表单数据
			formData: {
				usernameOrEmail: {value:"",validate:""},//赋值对象，包含验证结果描述
				password: {value:"",validate:""},
				captcha: {value:"",validate:""},
			},
			captchaUrl:""
		};
	},
	methods: {
		refreshCaptchaUrl(){
			this.captchaUrl = this.api.user.getCaptchaUrl()
		},
		blurValidate(el){
			const {id,value} = el.target
			this.formData[id].validate=this.validate(id,value)
		},
		validate(name,value){
			switch(name){
				case "usernameOrEmail":return validator.username(value); break;
				case "password":return validator.password(value); break;
				case "captcha":return validator.securityCode(value); break;
			}
			return ""
		},
		doLogin(){
			if(!validator.validateAll(this.formData,this.validate)) return
			
			const dto={
				usernameOrEmail:this.formData.usernameOrEmail.value,
				password:this.formData.password.value,
				captcha:this.formData.captcha.value
			}
			
			this.api.user.login(dto,(data)=>{
				const {user,token} = data
				this.vuex.set("user",user)
				this.vuex.set("isLogin",true)
				this.vuex.set("token",token)
				this.util.routeBack()
				this.util.toast(user.username+" 已成功登录")
			})
		},
		toRegisterPage(){
			this.util.routeTo("Register")
		},
		toForgetPwdPage(){
			this.util.routeTo("ForgetPwd")
		}
	},
	onReady() {
		this.refreshCaptchaUrl();
	}
};
</script>

<style lang="scss">

@import "@/common/my-class.scss";
@import "@/pages/Self/_common.scss";

.captcha{
	height: 50rpx;
	border:1px solid;
	border-color: dimgray;
}
</style>
