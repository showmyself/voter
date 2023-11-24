<template>
	<view class="my-page-root">
		<my-nav-bar title="注册"/>
		<view class="my-page-root-child form-page">

			<view style="height: 20rpx;"/>

			<view class="form-area">

				<view class="form-item">
					<text class="lable">用户名</text>
					<!-- id对于验证有用 -->
					<input class="input" id="username" v-model="formData.username.value" type="text" @blur="blurValidate"/>
					<my-error-info :info="formData.username.validate"/>
				</view>

				<view class="form-item">
					<text class="lable">密码</text>
					<input class="input" id="password" v-model="formData.password.value" type="password" @blur="blurValidate"/>
					<my-error-info :info="formData.password.validate"/>
				</view>

				<view class="form-item">
					<text class="lable">邮箱</text>
					<input class="input" id="email" v-model="formData.email.value" type="text" @blur="blurValidate"/>
					<my-error-info :info="formData.email.validate"/>
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
					<button class="confirm-button" @click="doRegister">确认注册</button>
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
				username: {value:"",validate:""},//赋值对象，包含验证结果描述
				password: {value:"",validate:""},
				email: {value:"",validate:""},
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
				case "username":return validator.username(value); break;
				case "password":return validator.password(value); break;
				case "email":return validator.email(value); break;
				case "validCode":return validator.securityCode(value); break;
			}
			return ""
		},
		doRegister(){
			if(!validator.validateAll(this.formData,this.validate)) return
			
			const dto={
				username:this.formData.username.value,
				password:this.formData.password.value,
				email:this.formData.email.value,
				validCode:this.formData.validCode.value,
			}
			
			this.api.user.register(dto,(data)=>{
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
			this.api.user.sendValidCodeToEmail(this.formData.email.value)
		}
	}
};
</script>

<style lang="scss">

@import "@/common/my-class.scss";
@import "@/pages/Self/_common.scss";

</style>
