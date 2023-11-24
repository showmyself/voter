//每个组件的公共动作提取
export default {
	onReady() {
		//网搜发现uniapp无法使用代码添加样式，那么想自动给父组件添加样式失败，只好在每个page中多包围一层view
		
		console.log("mixin action")
		
		uni.showTabBar()
	},
}