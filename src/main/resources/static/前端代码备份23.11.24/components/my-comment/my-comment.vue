<template>
	<view style="box-shadow: rgba(0, 0, 0, 0.24) 0px 3px 8px;" v-if="comment">
		<view class="my-flex-usual" style="align-items: center;">
			<view style="width: 10%;">
				<u-avatar :size="45" :src="comment.user.iconSrc"></u-avatar>
			</view>
			<view style="width: 45%;color:royalblue">
				<text>{{comment.user.username}}</text>
			</view>
			<view style="width: 35%;">
				<text style="font-size: 80%;color:gray">{{showTimeFor(comment)}}</text>
			</view>
			<view style="width: 5%;">
				<image @click="like" mode="widthFix" class="icon" :style="[st.icon()]" :src="require((cmt&&cmt.liked)?'@/static/点赞过.png':'@/static/点赞.png')"/>
			</view>
			<view style="width: 5%;">
				<text>{{cmt.likeCount}}</text>
			</view>
		</view>
		
		<view style="font-size: 90%;font-weight: 500;">
			{{comment.remark}}
		</view>
		
		<view v-if="comment.replyId" style="width: 80%;box-shadow: rgba(0, 0, 0, 0.24) 0px 3px 8px;" class="my-at-center-horizontal-block">
			<view v-if="!comment.repliedComment" style="font-size: 90%;font-weight: 500;">
				<view style="margin-top: 10rpx;"/>
				此评论已被删除
			</view>
			<view v-if="comment.repliedComment">
				<view style="margin-top: 10rpx;"/>
				
				<view>
					<u-avatar :size="45" :src="comment.repliedComment.user.iconSrc"></u-avatar>
					<text style="margin-left: 3%;">{{comment.repliedComment.user.username}}</text>
				</view>
				
				<view style="font-size: 90%;font-weight: 500;">
					{{comment.repliedComment.remark}}
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name:"my-comment",
		props:{
			comment:{
				type:Object
			},
		},
		data() {
			return {
				cmt:{//这两者会动态变化并渲染，所以单独提取出来
					likeCount:0,
					liked:false,
				},
				
				userId:null//当前登录用户id
			};
		},
		methods:{
			showTimeFor(comment){
				return this.util.removeSecondForTimeStr(comment.remarkTime)
			},
			like(){
				if(!this.cmt.liked){
					this.api.comment.like(this.comment.id,()=>{
						this.cmt.likeCount+=1
						this.cmt.liked=true
					})
				}
				else{
					this.api.comment.cancelLike(this.comment.id,()=>{
						this.cmt.likeCount-=1
						this.cmt.liked=false
					})
				}
			}
		},
		beforeMount(){
			this.cmt=this.comment
			if(this.vuex.get("isLogin")){
				this.userId=this.vuex.get("user").id
			}
		}
	}
</script>

<style lang="scss">

</style>