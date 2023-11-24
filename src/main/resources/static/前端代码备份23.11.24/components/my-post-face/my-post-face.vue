<template>
	<view class="box my-center-one">
		<view @click="intoPost" style="width: 92%;">
			<view style="margin-top: 3%;"></view>
			
			<view class="title">{{post.title}}</view>
			<view class="content">{{getContentShow()}}</view>
			<view :class="post.finished?'finished':'not-finished'"  space="ensp">{{post.finished?"◉ 已结束  ":"● 进行中...  "}}</view>
			<view class="view-count"  space="ensp">{{"浏览量  "+post.viewCount}}</view>
			
			<view style="margin-top: 3%;"></view>
		</view>
		
		<u-modal v-model="isInputKey"
		:title-style="{color: 'cornflowerblue'}"
		@confirm="confirmIntoPostWithKey"
		@cancel="isInputKey=false"
		:show-cancel-button="true">
			<view class="slot-content" style="width: 90%;margin:0 auto;">
				<text style="font-size: 80%;font-weight: bold">需要秘钥：</text>
				<input style="background-color: lightgray;border-radius: 8rpx;" type="password" v-model="inputKey">
			</view>
		</u-modal>
		
	</view>
</template>

<script>
	export default {
		name:"my-post-face",
		props:["post"],
		data() {
			return {
				isInputKey:false,
				inputKey:"",
			};
		},
		methods:{
			getContentShow(){
				if(this.post.content){
					if(this.post.content.length>30){
						return this.post.content.substring(0,30)+" . . ."
					}
					else return this.post.content
				}
				return ""
			},
			intoPost(){
				this.api.post.tryByNumber(this.post.number,this.post.key,(data)=>{
					this.util.routeTo("PostDetails",{id:this.post.id,key:this.post.key})
				},()=>{
					this.isInputKey=true
				})
			},
			confirmIntoPostWithKey(){
				this.api.post.tryByNumber(this.post.number,this.inputKey,(data)=>{
					this.util.routeTo("PostDetails",{id:this.post.id,key:this.inputKey})
				},()=>{
					this.isInputKey=false
					this.util.toast("秘钥错误")
				})
			},
		}
	}
</script>

<style lang="scss" scoped>

.box{
	border: 5px solid lightgray;
	margin-top: 1%;
}

.title{
	color:royalblue;
	font-weight: 650;
	font-size: 120%;
}

.content{
	color:dimgray;
	font-weight: 600;
	font-size: 80%;
	white-space: pre;
}

.not-finished{
	float:right;
	color:seagreen;
	font-weight: 600;
	font-size: 80%;
}

.finished{
	float:right;
	color:indianred;
	font-weight: 600;
	font-size: 80%;
}

.view-count{
	color:black;
	font-weight: 300;
	font-size: 80%;
}

</style>