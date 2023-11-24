<template>
	<view v-if="info?true:false">
		<text class="error-info">{{info?"*"+info:""}}</text>
	</view>
</template>

<script>
	export default {
		name:"my-error-info",
		props:["info"]
	}
</script>

<style lang="scss" scoped>
.error-info{
	color:red;
	font-size: 60%;
	font-weight: 600;
}
</style>