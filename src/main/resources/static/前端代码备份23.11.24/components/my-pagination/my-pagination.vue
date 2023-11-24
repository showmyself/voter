<template>
	<view class="my-flex-usual" style="margin-top: 2%;">
		
		<view style="width: 40%;"> </view>
		
		<view class="my-center-horizontal-one" style="width: 20%;">
			<a class="search-more" @click="searchMore">点击查看更多</a>
		</view>
		
		<view style="width: 40%;">
			<view style="margin-top: 6rpx;"/>
			<a class="set-start-page" @click="isInputStartPage=true">（设定起始页）</a>
		</view>
		
		<u-modal v-model="isInputStartPage"
		:title-style="{color: 'cornflowerblue'}"
		title="起始页号"
		@confirm="searchStart"
		@cancel="isInputStartPage=false"
		:show-cancel-button="true">
			<view class="slot-content my-center-one" style="height: 80rpx;">
				<input class="input-start-page" v-model="startPage">
			</view>
		</u-modal>
	</view>
</template>

<script>
	export default {
		name:"my-pagination",
		//需传一个带有{targetPage,newStart}形参的@searchPage函数
		//本组件就不显示最大页数了，如果要显示会变得非常麻烦（已实测），不如在@searchPage内部显示提醒即可，具体参考SelfPosts页面
		data() {
			return {
				startPage:1,//当前设定的起始页
				currentPage:1,//当前搜索出的最大页号
				
				isInputStartPage:false
			};
		},
		methods:{
			
			//搜索更多
			searchMore(){
				this.currentPage+=1
				
				this.$emit("searchPage",{
					targetPage:this.currentPage,
					newStart:false
				})
			},
			
			//从设定的起始页开始搜
			searchStart(){
				//格式检查
				if(!this.startPage)this.startPage=1
				this.startPage=parseInt(this.startPage)
				if(this.startPage<1)this.startPage=1
				
				this.currentPage=this.startPage
				this.$emit("searchPage",{
					targetPage:this.startPage,
					newStart:true
				})
			},
			
			//供外部调用，外部赋值该组件一个ref="xxx"，然后即可使用this.$ref.xxx.reset()
			reset(){
				this.startPage=1
				this.searchStart()
			}
		}
	}
</script>

<style lang="scss" scoped>

.search-more{
	color:slategray;
	font-size: 80%;
	font-weight: bold;
	text-decoration: underline;
}

.set-start-page{
	color:slategray;
	font-size: 60%;
	font-weight: 600;
	margin: 0 auto;
	border-width: 0px;
}

.start-page{
	font-size: 80%;
	font-weight: 660;
}

.input-start-page{
	display: inline-table;
	background-color: #ccc;
	border-radius: 5rpx;
	height: 22rpx;
	width: 20%;
	font-size: 80%;
	user-select: all;
}

</style>
