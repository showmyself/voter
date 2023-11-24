<template>
	<view class="my-page-root">
		<my-nav-bar title="个人评论"/>
		<view class="my-page-root-child">

			<my-empty v-if="comments.length==0" type="comment" text="评论为空"/>

			<view v-for="cmt,index in comments" :key="cmt.id">
				<view style="margin-top: 40rpx;"/>
				<my-comment :comment="cmt"/>
				<view style="margin-top: 10rpx;"/>
				
				<view class="my-flex-usual">
					<view style="width: 1%;"/>
					<view style="width: 70%;">
						<button @click="()=>{intoPost(index)}">前往帖子</button>
					</view>
					<view style="width: 1%;"/>
					<view style="width: 27%;">
						<button style="color:#f99" @click="()=>{isDeleteComment=true;targetCommentIndex=index}">删除</button>
					</view>
					<view style="width: 1%;"/>
					<!--虽然详情页面里已有删除操作，但此处还是打算保留-->
				</view>
				
			</view>

			<u-modal v-model="isDeleteComment"
			:title-style="{color: 'cornflowerblue'}"
			@confirm="confirmDeleteComment"
			@cancel="isDeleteComment=false"
			:show-cancel-button="true">
				<view class="my-center-one">
					<text style="color:red;">确定删除？</text>
				</view>
			</u-modal>

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
			
			<my-pagination @searchPage="searchPage" ref="mypagination"/>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				comments:[],
				pageSize:3,
				targetCommentIndex:-1,
				isDeleteComment:false,
				
				isInputKey:false,
				inputKey:"",
			};
		},
		methods:{
			searchPage({targetPage,newStart}){
				this.api.comment.findPresentUserComments(targetPage,this.pageSize,(data)=>{
					const succ = this.util.dealPage(targetPage,this.pageSize,data.total)
					if(succ){
						if(newStart)
							this.comments=data.comments
						else
							this.comments=[...this.comments,...data.comments]
					}
				})
			},
			toPage1(){
				if(this.$refs.mypagination)
					this.$refs.mypagination.reset()
			},
			intoPost(index){
				this.targetCommentIndex=index
				const postNumber = this.comments[index].postNumber
				this.api.post.tryByNumber(postNumber,"",(data)=>{
					this.util.routeTo("PostDetails",{id:this.comments[index].postId,key:""})
				},()=>{
					this.isInputKey=true
				})
			},
			confirmIntoPostWithKey(){
				const postNumber = this.comments[this.targetCommentIndex].postNumber
				this.api.post.tryByNumber(postNumber,this.inputKey,(data)=>{
					this.util.routeTo("PostDetails",{id:this.comments[this.targetCommentIndex].postId,key:this.inputKey})
				},()=>{
					this.isInputKey=false
					this.util.toast("秘钥错误")
				})
			},
			confirmDeleteComment(){
				this.api.comment.delete(this.comments[this.targetCommentIndex].postId,(succ)=>{
					this.util.toast("已删除")
					this.toPage1()
				})
			},
		},
		onLoad(){
			this.toPage1()
		}
	}
</script>

<style lang="scss">

</style>
