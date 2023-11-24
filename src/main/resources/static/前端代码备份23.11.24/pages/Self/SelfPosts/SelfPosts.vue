<template>
	<view class="my-page-root">
		<my-nav-bar title="个人发帖"/>
		<view class="my-page-root-child">

			<view style="margin-top: 8rpx;"/>
			<button style="width: 40%;" class="my-at-center-horizontal-block" @click="toAdd()">新建投票帖</button>

			<my-empty v-if="posts.length==0" type="post"/>

			<view v-for="post,index in posts" :key="post.id" >
				<my-post-face :post="post"/>
				<view class="my-flex-usual">
					<view style="width: 1%;"/>
					<view style="width: 70%;">
						<button @click="()=>{toEdit(index)}">编辑</button>
					</view>
					<view style="width: 1%;"/>
					<view style="width: 27%;">
						<button @click="()=>{toDelete(index)}" style="color:#f99">删除</button>
					</view>
					<view style="width: 1%;"/>
					<!--虽然详情页面里已有删除操作，但此处还是打算保留-->
				</view>
			</view>
			<my-pagination @searchPage="searchPage" ref="mypagination"/>

			<u-modal v-model="isDelete"
			:title-style="{color: 'cornflowerblue'}"
			@confirm="confirmDelete"
			@cancel="isDelete=false"
			:show-cancel-button="true">
				<view>
					<view style="margin-top: 3%;"/>
					<view class="my-center-one" style="color: red;">确定删除？</view>
					<view style="margin-top: 3%;"/>
				</view>
			</u-modal>
		
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			//翻页相关需2条数据：
			posts:[],
			pageSize:2,
			
			isDelete:false,
			deleteIndex:-1,
			isEdit:false,
		};
	},
	methods:{
		toAdd(){
			this.api.post.canCurrentUserAddPost((data)=>{
				const can = data
				if(can)
					this.util.routeTo('EditPost')
				else
					this.util.toast("积分不足！参与投票可获取积分",1000)
			})
		},
		toEdit(index){
			this.util.routeTo("EditPost",{editId:this.posts[index].id,editKey:this.posts[index].key})
		},
		toDelete(index){
			this.deleteIndex=index
			this.isDelete=true
		},
		confirmDelete(){
			this.api.post.delete(this.posts[this.deleteIndex].id,()=>{
				this.posts.splice(this.deleteIndex,1)
				this.util.toast("删除成功！")
			})
		},
		
		searchPage({targetPage,newStart}){
			this.api.post.findCurrentUserPosts(targetPage,this.pageSize,(data)=>{
				const succ = this.util.dealPage(targetPage,this.pageSize,data.total)

				if(succ){
					if(newStart)
						this.posts=data.posts
					else
						this.posts=[...this.posts,...data.posts]
				}
			})
		},
		toPage1(){
			if(this.$refs.mypagination)
				this.$refs.mypagination.reset()
		},
		backRefresh(){
			this.toPage1()
		}
	},
	onReady(){
		this.toPage1()
	}
}
</script>

<style lang="scss">

</style>
