<template>
	<view>
		<view v-for="option,index in options" :key="index">
			<view class="bar" :style="[getStyle(index)]">
				<text style="font-size: 60%;" space="ensp">{{option.count+"   "}}</text>
				<text style="font-size: 70%;font-weight: 640;">{{option.desc}}</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name:"my-bar-chart",
		props:{
			options: {
			  type: Array,
			  default: () => [{desc:"a",count:1},{desc:"b",count:2}]//格式
			}
		},
		data() {
			return {
				counts:[],//暂存每个选项的count，对做动画有用
				total:0,
			}
		},
		methods:{
			getStyle(index){
				return {
					width: this.total>0?Math.floor(100*this.counts[index]/this.total)+"%":"1%",
				}
			}
		},
		beforeMount(){
			this.total=0
			
			const counts=[]
			this.counts=[]
			
			for(const option of this.options){
				this.total+=option.count
				counts.push(option.count)
				this.counts.push(0)//先让this.counts每个元素为0
			}
			
			//延迟一会儿后再设置为最终值，使有动画渐变
			setTimeout(()=>{this.counts=[...counts]},100)

		}
	}
</script>

<style lang="scss" scoped>
.bar{
	margin-top: 20rpx;
	background-color:skyblue;
	overflow:'visible';
	white-space:nowrap;
	border-radius: 4rpx;
	height: 40rpx;
	display: flex;
	align-items: center;
	
	transition: width 0.3s;
	//无法使用css的@keyframes，因为该组件的bar宽度是动态变化的，而css是静态没法做到
}

</style>