<template>
	<view class="my-page-root">
		<my-nav-bar title="同系列帖子"/>
		<view class="my-page-root-child">

			<my-empty v-if="posts.length==0" type="post"/>
			<view v-for="post,index in posts" :key="post.id" >
				<my-post-face :post="post"/>
			</view>
		
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			posts:[],
			
			isDelete:false,
			deleteIndex:-1,
			
			//暂存系列名以及所属用户id
			userId:null,
			series:"",
		};
	},
	methods:{
		load(){
			this.api.post.findAllPostsInSameSeries(this.userId,this.series,(data)=>{
				this.posts=data.posts
			})
		},
		backRefresh(){
			this.load()
		}
	},
	onLoad({userId,series}){
		this.userId=userId
		this.series=series
		this.load()
	}
}
</script>

<style lang="scss">

</style>
