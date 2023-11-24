<template>
	<view>
		<!--uview1.x的上传组件仅支持图片，日后要上传文件的话另需网搜学习-->
		<!--打印ref，发现有些与上传类型相关的参数，日后有需求的话可以试一试-->
		<!--自己定义的上传组件始终仅上传一个-->
		<u-upload
		v-show="progress<=0"
		ref="uploadRef"
		:action="url"
		:max-size="maxSize"
		max-count="1"
		:custom-btn="true"
		:show-upload-list="false"
		:before-upload="thisBeforeUpload"
		@on-progress="uploading"
		@on-uploaded="thisOnUploaded">
			<!--自定义上传按钮-->
			<view slot="addBtn">
				<a class="upload-btn">上传</a>
			</view>
		</u-upload>
		<u-line-progress v-show="progress>0" active-color="#29aaff" height="10" :percent="progress" :show-percent="false"></u-line-progress>
	</view>
</template>

<script>
	export default {
		name:"my-upload-img",
		//外部传入四个参数：:url、:maxSize、@beforeUpload、@onUploadSuccess（注意最好不要使用参数形式传入函数，可能导致this指针错误）
		props:["url","maxSize"],
		data() {
			return {
				ref:{},
				progress:0,
			};
		},
		methods:{
			thisBeforeUpload(index,list){
				//实测发现上传ico类型时，此函数没有执行且结果列表长度为0，原因不明也暂时不管
				if(list.length<0) return false
				
				//有个bug：微信小程序端，不知为何上传时this.ref丢失了
				//找到原因了，before-upload是用参数形式来传递函数（u-upload设定的，没法改），所以this丢失了
				//为了解决这个问题，全局存储this.ref，这样就能拿到
				
			    //下列方式可用来获取beforeUpload的返回值（u-upload组件要用），详细网搜vue $emit如何赋值返回值
				let result = null
				const ref = this.util.getTmpData("uploadRef")//mounted周期已存入，此处取出即可
				this.$emit("beforeUpload",{
					list:ref.lists, clear:ref.clear
					//clear函数用来清空本地列表（因为我设置的最大上传数为1，所以需要先清空才能继续上传），
					//如果beforeUpload内部要阻止上传，则接着调用clear()，才能继续下一次上传
				},val => {result = val})
					
			    return result
			},
			uploading(){
				this.progress=this.getUploadProgress()
			},
			thisOnUploaded(list){
				if(list.length>0){
					const res=list[0].response
					if(res){//如果res为空，会自动提示上传失败，请重试，所以这里不用再显示
						if(res.flag){
							this.util.toast("上传成功")
							this.$emit("onUploadSuccess",res.data)
						}
						else{
							this.util.toast("失败："+res.msg)
						}
					}
				}
				
				this.progress=0
				
				if(this.ref)
					this.ref.clear()
				else this.clear()
			},

			//获得最近的文件上传进度
			getUploadProgress(){
				const list = this.ref.lists
				if(list){
					const length = list.length
					if(length>0){
						const item = list[length-1]
						return item.progress
					}
				}
				return 0
			},
		},
		mounted(){
			this.ref = this.$refs.uploadRef
			
			this.util.setTmpData("uploadRef",this.ref)//暂存到全局，详细参考上文thisBeforeUpload
		}
	}
</script>

<style lang="scss" scoped>
.upload-btn{
	color:steelblue;
	font-size: 80%;
}
</style>