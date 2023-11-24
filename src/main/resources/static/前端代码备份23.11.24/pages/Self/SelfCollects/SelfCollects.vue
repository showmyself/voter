<template>
	<view class="my-page-root">
		<my-nav-bar title="个人收藏"/>
		<view class="my-page-root-child">
		
			<my-empty v-if="posts.length==0" type="collect"/>
			<view v-for="post,index in posts" :key="post.id" >
				<my-post-face :post="post"/>
			</view>
			<my-pagination @searchPage="searchPage" ref="mypagination"/>
		
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			posts:[],
			pageSize:2,
		};
	},
	methods:{
		searchPage({targetPage,newStart}){
			//关于翻页说明可以参考Visit.vue
			this.api.post.findPostsInCollects(targetPage,this.pageSize,(data)=>{

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
		},
	},
	onReady(){
		this.toPage1()
	}
}
</script>

<style lang="scss">

</style>
