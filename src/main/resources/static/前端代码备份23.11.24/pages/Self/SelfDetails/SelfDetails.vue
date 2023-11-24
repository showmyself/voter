<template>
	<view class="my-page-root">
		<my-nav-bar title="个人信息"/>
		<view class="my-page-root-child form-page">

			<view style="height: 20rpx;"/>

			<view class="form-area">

				<view class="form-item">
					<view style="display: inline-block;">
						<u-avatar :src="isEdit?userEdit.iconSrc:user.iconSrc"></u-avatar>
					</view>
					<view v-if="isEdit" style="display: inline-block;width: 70%; margin-left: 5%;">
						<my-upload-img
						:url="uploadUrl"
						:maxSize="0.5 * 1024 * 1024"
						@beforeUpload="beforeUpload"
						@onUploadSuccess="onUploadSuccess">
						</my-upload-img>

						<view>
							<text class="icon-link">当前图片链接：</text>
						</view>
						<view>
							<input id="iconSrc" class="input-icon-src" v-model="userEdit.iconSrc" type="text"/>
						</view>

					</view>
				</view>
				
				<view class="form-item">
					<text class="lable">用户名</text>
					<view style="display: inline-table;margin-left: 5%;width: 80%;">
						<input class="input" v-if="!isEdit" v-model="user.username" type="text" :disabled="true" :style="[{'background-color':'#eee'}]"/>
						<input class="input" v-if="isEdit" v-model="userEdit.username" type="text" :style="[{'background-color':'#ddd'}]"/>
					</view>
				</view>

				<view class="form-item">
					<text class="lable">性别</text>
					
					<view style="display: inline-table;margin-left: 5%;">
						<a class="select-btn" :style="[{'text-decoration': isEdit?'underline':'none'}]"
							@click="()=>{if(isEdit)isSelectSex = true}">
							{{getSex()}}
						</a>
						
						<u-select v-model="isSelectSex" :list="sexList" @confirm="confirmSex"></u-select>
					</view>
				</view>

				<view class="form-item">
					<text class="lable">年龄</text>
					
					<view style="display: inline-table;margin-left: 5%;">
						<a class="select-btn" :style="[{'text-decoration': isEdit?'underline':'none'}]"
							@click="()=>{if(isEdit)isSelectAge = true}">
							{{getAge()}}
						</a>
						
						<u-select v-model="isSelectAge" :list="ageList" @confirm="confirmAge"></u-select>
					</view>
				</view>
				
				<view class="form-item">
					<view style="margin-top: 10rpx;">
						<text class="lable">邮箱</text>
						<text class="value">{{user.email}}</text>
					</view>
					<view style="margin-top: 10rpx;">
						<text class="lable">当前积分</text>
						<text class="value mark">{{user.mark}}</text>
					</view>
					<view style="margin-top: 10rpx;">
						<text class="lable">创建日期</text>
						<text class="value">{{user.createTime}}</text>
					</view>
				</view>
				
				<view style="height: 60rpx;"/>

				<view>
					<a v-if="!isEdit" class="change-pwd-btn" style="float:left" @click="toChangePwd">修改密码</a>
					<u-modal v-model="isChangePwd" 
					:title-style="{color: 'cornflowerblue'}"
					@confirm="confirmChangePwd"
					:show-cancel-button="true">
						<view class="slot-content">
							<text class="pwd-label">当前密码</text>
							<input class="pwd-input" type="password" v-model="oldPwd"/>
							<view style="margin-top: 30rpx;"></view>
							<text class="pwd-label">新密码</text>
							<input class="pwd-input" type="password" v-model="newPwd"/>
							<view style="margin-top: 20rpx;"></view>
						</view>
					</u-modal>
					
					<button v-if="!isEdit" class="confirm-button" style="float:right" @click="beginEdit">修改资料</button>
					
					<button v-if="isEdit" class="confirm-button" style="float:right" @click="confirmEdit">确认修改</button>
					<a v-if="isEdit" class="canel-edit-link" style="float:right;margin-right: 5%;margin-top: 15rpx;" @click="cancelEdit">取消修改</a>
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
			user:{
				id:"",
				iconSrc:"",
				username:"",
				age:"",
				sex:"",
				email:"",
				mark:0,
				createTime:""
			},
			userEdit:{
				id:"",
				iconSrc:"",
				username:"",
				age:"",
				sex:"",
			},
			sexList:[
				{
					value: '男',
					label: '男 '
				},
				{
					value: '女',
					label: '女 '
				},
				{
					value: '不透露',
					label: '不透露'
				},
			],
			ageList:[],
			isEdit:false,
			isSelectSex:false,
			isSelectAge:false,
			isChangePwd:false,
			uploadUrl:"",
			oldPwd:"",
			newPwd:""
		};
	},
	methods: {
		getSex(){
			const sex = this.isEdit?this.userEdit.sex:this.user.sex
			return sex?sex:"不透露"
		},
		confirmSex(option){
			this.userEdit.sex=option[0].value
		},
		getAge(){
			const age = this.isEdit?this.userEdit.age:this.user.age
			if(!age||age<=0)return "不透露"
			return age
		},
		confirmAge(option){
			this.userEdit.age=option[0].value
		},
		beginEdit(){
			for(const key in this.userEdit){
				this.userEdit[key]=this.user[key]
			}
			this.isEdit=true
		},
		cancelEdit(){
			this.isEdit=false
			this.deleteLatestAvatar()
		},
		confirmEdit(){
			if(this.userEdit.username.length<3){
				this.util.toast("用户名 长度至少为3")
				return
			}
			if(this.userEdit.username.length>20){
				this.util.toast("用户名 长度过长")
				return
			}
			if(!this.userEdit.age)this.userEdit.age=0
			if(!this.userEdit.sex)this.userEdit.sex=""
			if(!this.userEdit.iconSrc)this.userEdit.iconSrc=""

			this.api.user.changeSelfInfo(this.userEdit,()=>{
				this.util.toast("修改成功")
				for(const key in this.userEdit){
					this.user[key]=this.userEdit[key]
				}
				this.vuex.set("user",this.user)
				this.isEdit=false
			})
		},
		refreshUser(){
			const user = this.vuex.get("user")
			for(const key in this.user){
				this.user[key]=user[key]
			}
		},

		//上传之前
		beforeUpload(data,callback){
			//本来打算在此处判定文件后缀名的，但发现uview1.x的上传组件貌似仅支持图片，所以此处不再判定
			
			callback(true)//用来赋值返回值，详细网搜vue $emit返回值
		},
		//上传完成时
		onUploadSuccess(resultData){
			this.deleteLatestAvatar()
			this.userEdit.iconSrc = resultData.url
		},
		deleteLatestAvatar(){
			//如果不一致则请求删除
			if(this.user.iconSrc!=this.userEdit.iconSrc&&this.userEdit.iconSrc){
				const names = this.userEdit.iconSrc.split('/')
				this.api.user.deleteAvatar(names[names.length-1])
				this.userEdit.iconSrc=null
			}
		},
		toChangePwd(){
			this.isChangePwd=true
		},
		confirmChangePwd(){
			if(this.oldPwd.length<4){
				this.util.toast("当前密码 长度至少为3")
				return
			}
			if(this.oldPwd.length>36){
				this.util.toast("当前密码 长度过长")
				return
			}
			if(this.newPwd.length<4){
				this.util.toast("新密码 长度至少为3")
				return
			}
			if(this.newPwd.length>36){
				this.util.toast("新密码 长度过长")
				return
			}
			
			const dto={
				oldPassword:this.oldPwd,
				newPassword:this.newPwd
			}
			
			this.api.user.changePassword(dto,(data)=>{
				this.util.toast("修改成功！")
				this.isChangePwd=false
			})
		}
	},
	onReady(){
		this.refreshUser()
		this.isEdit=false
		this.uploadUrl = this.api.user.getUploadAvatarUrl()
		this.ageList=[{value:-1,label:"不透露"}]
		for(let i = 5;i<=110;++i){
			this.ageList.push({value:i,label:i.toString()})
		}
	},
	onUnload() {
		//路由返回会导致页面的卸载
		this.deleteLatestAvatar()
	},
};
</script>

<style lang="scss" scoped>

@import "@/common/my-class.scss";
@import "@/pages/Self/_common.scss";

.upload-btn{
	color:steelblue;
	font-size: 90%;
}
.icon-link{
	font-size: 50%;
	font-weight: 600;
	color:#999;
}
.input-icon-src{
	border-radius: 0rpx;
	color:skyblue;
	height: 15rpx;
	text-decoration: underline;
}
.select-btn{
	font-size: 70%;
	color:dimgray;
	font-weight: 600;
	border-width: 0px;
}
.value{
	margin-left: 5%;
	font-size: 60%;
	font-weight: 400;
}
.canel-edit-link{
	font-size: 70%;
	color:#888;
	font-weight: 600;
}
.change-pwd-btn{
	font-size: 70%;
	color:#aaa;
	font-weight: 500;
	text-decoration: underline;
}
.pwd-label{
	font-size: 60%;
	margin-left: 10%;
	font-weight: 600;
	border-radius: 20%
}
.pwd-input{
	@extend .my-at-center-horizontal-block;
	width: 80%;
	height: 35rpx;
	background-color: lightgray;
	border-radius: 10rpx;
}
</style>
