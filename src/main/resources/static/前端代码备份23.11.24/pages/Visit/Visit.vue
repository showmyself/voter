<template>
	<view class="my-page-root">
		<my-nav-bar title="浏览" :removeBack="true"/>
		<view class="my-page-root-child">
			<view style="display: flex;align-items: center;border: 1px solid gray;margin-top: 3rpx;height:50rpx">
				
				<view style="width: 5%;"/>
				
				<view style="width: 15%;">
					<a class="set-category" @click="openSelectCategory">{{category?category:"选择类别"}}</a>
					<u-select v-model="isSelectCategory" :list="categoryList" @confirm="confirmCategory"></u-select>
				</view>
				
				<view style="width: 20%;">
					<a class="set-time" @click="confirmOrderType">{{timeOrHot=="hot"?"按热度排序":"按时间排序"}}</a>
				</view>
				
				<view style="width: 5%;"/>
				
				<view style="width: 40%;">
					<text class="keyword">关键字</text>
					<input class="input-keyword" v-model="keyword">
				</view>
				
				<view style="width: 10%;" class="my-center-vertical-one">
					<image mode="widthFix" :style="[st.icon()]" :src="require('@/static/搜索.png')" @click="doSearch"/>
				</view>

			</view>
			
			<view>
				<button class="direct-to-btn" @click="isOpenTarget=true">
					<!-- <image mode="widthFix" :style="[st.icon()]" :src="require('@/static/右双箭头.png')" @click="doSearch"/> -->
					✈ 编号直达
				</button>
				
				<u-modal v-model="isOpenTarget"
				title="编号直达"
				:title-style="{color: 'cornflowerblue'}"
				@confirm="confirmIntoTargetPost"
				@cancel="isOpenTarget=false"
				:show-cancel-button="true">
					<view class="slot-content my-center-one">
						<view style="width: 80%;">
							
							<view style="margin-top: 10rpx;"/>
							<view>
								<text class="number">编号：</text>
								<input class="direct-input" v-model="number">
							</view>
							
							<view style="margin-top: 10rpx;"/>
							<view>
								<text class="key">秘钥：</text>
								<input class="direct-input" v-model="key" type="password">
							</view>
							
							<view style="margin-top: 10rpx;"/>
						</view>
					</view>
				</u-modal>
			</view>
			
			<view class="result-list-label">
				结果列表
			</view>

			<my-empty v-if="posts.length==0" type="search"/>
			<view v-for="post,index in posts" :key="index">
				<my-post-face :post="post"/>
			</view>
			
			<my-pagination @searchPage="searchPage" ref="mypagination"/>
		<!-- 把 板块分区、搜索、编号直达 都放在该页面处理 -->
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				posts:[],
				pageSize:4,
				
				keyword:"",
				category:"",
				timeOrHot:"",
				
				isSelectCategory:false,
				categoryList:[],
				
				isOpenTarget:false,
				number:"",
				key:"",
			}
		},
		methods: {
			doSearch(){
				this.toPage1()
			},
			openSelectCategory(){
				this.categoryList = [{label:"选择类别",value:""}]
				for(const cate of this.constant.categories){
					this.categoryList.push({label:cate,value:cate})
				}
				this.isSelectCategory=true
			},
			confirmCategory(option){
				this.category = option[0].value
				this.doSearch()
			},
			confirmOrderType(){
				this.timeOrHot=this.timeOrHot=='hot'?'time':'hot'
				this.doSearch()
			},
			confirmIntoTargetPost(){
				this.api.post.tryByNumber(this.number,this.key,(data)=>{
					this.util.routeTo("PostDetails",{id:data.id,key:this.key})
				},()=>{
					this.util.toast("编号或秘钥不正确")
				})
			},
			
			//注意，如果使用了my-pagination，一般searchPage、toPage1、backRefresh()需要成套出现
			searchPage({targetPage,newStart}){
				this.api.post.findPosts(targetPage,this.pageSize,this.keyword,this.category,this.timeOrHot,(data)=>{

					//后端分页查询有个问题，当currentPage大于总page数时，并非不会搜出数据，而是又从头开始搜
					//所以不能根据返回列表的长度判定 "是否已没有更多数据"，而是需要根据返回的total计算一下，这里调用一下通用处理
					const succ = this.util.dealPage(targetPage,this.pageSize,data.total)

					if(succ){
						if(newStart)
							this.posts=data.posts
						else
							this.posts=[...this.posts,...data.posts]//使用push失败，使用此成功
					}
				})
			},
			toPage1(){
				if(this.$refs.mypagination)
					this.$refs.mypagination.reset()
			},
			backRefresh(){//该函数在调用util.routeBack后自动被调用
				this.toPage1()
			}
		},
		onReady(){
			this.toPage1()
		},
		onPullDownRefresh:function(){
		    this.toPage1()
			uni.stopPullDownRefresh()
		},
	}
</script>

<style lang="scss" scoped>
	
.keyword{
	color:gray;
	font-size: 80%;
	font-weight: 600;
}

.input-keyword{
	display: inline-table;
	width: 70%;
	background-color: #ddd;
	border-radius: 8rpx;
	margin-left: 2%;
}

.set-category{
	color:gray;
	font-size: 80%;
	font-weight: 600;
	text-decoration: underline;
}

.set-time{
	color:gray;
	font-size: 80%;
	font-weight: 600;
	text-decoration: underline;
}

.result-list-label{
	// color:royalblue;
	font-size: 80%;
	font-weight: 600;
}

.direct-to-btn{
	// color:dimgray;
	font-size: 80%;
	font-weight: 500;
	height: 50rpx;
}

.number{
	color:black;
	font-size: 70%;
	font-weight: 600;
}

.key{
	color:black;
	font-size: 70%;
	font-weight: 600;
}

.direct-input{
	display: inline-table;
	background-color: #ddd;
	border-radius: 8rpx;
	margin-left: 2%;
}

</style>
