<template>
	<view class="option-box my-center-one">
		<view style="width: 95%;">
			<view style="margin-top: 2%;"/>
			
			<view class="desc">{{option.desc}}</view>
			<image v-if="option.picSrc" mode="aspectFit" style="max-width: 80%" :src="option.picSrc"/>
			<view v-if="option.link">
				<text class="refer-link-label">
					参考链接：
				</text>
				<text class="refer-link-value">
					{{option.link}}
				</text>
			</view>
			
			<view style="margin-top: 2%;"/>
		</view>
	</view>
</template>

<script>
	export default {
		name:"my-option-show",
		props:["option"],
		data() {
			return {
				
			};
		}
	}
</script>

<style lang="scss" scoped>

.option-box{
	box-shadow: rgba(0, 0, 0, 0.24) 0px 3px 8px;
}

.desc{
	color:dimgray;
	font-size: 90%;
	font-weight: 550;
}

.refer-link-label{
	color:black;
	font-size: 80%;
	font-weight: 400;
}
.refer-link-value{
	color:blue;
	font-size: 80%;
	font-weight: 500;
}

</style>