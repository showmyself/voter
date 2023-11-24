
<script>
	export default {
		onLaunch: function() {
			const token = this.vuex.get("token")
			
			if(token){
				this.api.user.getSelf((data)=>{
					const user=data
					this.vuex.set("user",user)
					this.vuex.set("isLogin",true)
				})
			}
			else{
				this.vuex.setDefault("user")
				this.vuex.set("isLogin",false)
			}
		},
		onShow: function() {
		},
		onHide: function() {
		}
	}
</script>

<style lang="scss">
	/*每个页面公共css */
	@import "@/uview-ui/index.scss";

	@import "@/common/my-class.scss";

</style>
