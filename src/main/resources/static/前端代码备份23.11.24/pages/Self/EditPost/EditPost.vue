<template>
	<view class="my-page-root">
		<my-nav-bar :title="isAdd?'新建':'更新'"/>
		<view class="my-page-root-child form-page">

			<view style="height: 20rpx;"/>
			<text v-if="!isAdd" style="color:orangered;font-size: 60%;margin-left: 40%;">仅支持修改部分参数</text>

			<view class="form-area">

				<view class="form-item" v-if="isAdd">
					<text class="lable">名称</text>
					<input class="input" v-model="post.title" type="text"/>
				</view>
				
				<view class="form-item" v-if="isAdd">
					<text class="lable">内容</text>
					<u-input v-model="post.content" type="textarea" border height="100" :auto-height="true"/>
				</view>
				
				<view class="form-item" v-if="isAdd">
					<text class="lable">参考图</text>
					<view>
						<my-upload-img
						:maxSize="2 * 1024 * 1024"
						:url="uploadUrl"
						@beforeUpload="beforeUploadPostPic"
						@onUploadSuccess="onUploadPostPicSuccess">
						</my-upload-img>
					</view>
					<input class="input" type="text" v-model="post.picSrc"/>
					<image v-if="post.picSrc" mode="aspectFit" style="max-width: 100%" :src="post.picSrc"/>
					<button v-if="post.picSrc" @click="deleteLatestPostPic">清除</button>
				</view>
				
				
				<view class="form-item">
					<text class="lable">类别</text>
					<button @click="openSelectCategory">{{post.category?post.category:"不指定"}}</button>
					<u-select v-model="isSelectCategory" :list="categoryList" @confirm="confirmCategory"></u-select>
				</view>
				
				<view class="form-item">
					<text class="lable">编号</text>
					<input class="input" v-model="post.number" type="text"/>
					<button @click="()=>{post.number=util.getRandomStr(8).toUpperCase()}">随机生成</button>
				</view>
				
				<view class="form-item">
					<text class="lable">秘钥</text>
					<input class="input" v-model="post.key" type="text"/>
					<button @click="()=>{post.key=util.getRandomStr(12)}">随机生成</button>
				</view>
				
				<view class="form-item" v-if="isAdd">
					<text class="lable">时限</text>
					<view style="color:royalblue">
						{{post.endTime?post.endTime:"不限时间"}}
					</view>
					<view class="my-flex-usual">
						<button @click="showEndTime = true" style="width: 50%;">选择</button>
						<button @click="post.endTime = null" style="width: 50%;">不限</button>
					</view>
					<!--u-picker与u-select类似，推荐使用后者，但对于时间和地区的选区，就直接用picker-->
					<u-picker v-model="showEndTime" mode="time" :params="timeParams" @confirm="confirmEndTime"></u-picker>
				</view>
				
				<view class="form-item" v-if="isAdd">
					<text class="lable">最大投票数</text>
					<input class="input" style="color:royalblue" v-model="post.maxVoteCount" type="number"/>
					<view v-if="isAdd">
						<button @click="post.maxVoteCount = -1">不限</button>
					</view>
				</view>
				
				<view class="form-item my-flex-usual">
					<view class="lable" width="20%">允许评论</view>
					<view style="width: 20%;" class="my-center-one">
						<u-switch v-model="post.commentAble" :size="35"/>
					</view>
				</view>
				
				<view class="form-item">
					<text class="lable">系列</text>
					<input class="input" v-model="post.series" type="text"/>
					<button @click="openSelectSeries">选择</button>
					<u-action-sheet v-model="isSelectSeries" :list="seriesList" @click="confirmSeries"/>
					<!-- <u-select v-model="isSelectSeries" :list="seriesList" @confirm="confirmSeries"></u-select> -->
				</view>
				
				<view class="form-item" v-if="isAdd">
					<text class="lable">选项：</text>
					<view>
						<button @click="toAddOption">添加新选项</button>
					</view>
					
					<view v-if="isAdd">
						<u-modal v-model="isAddOrEditOption"
						:title-style="{color: 'cornflowerblue'}"
						@confirm="confirmAddOrEditOption"
						@cancel="cancelAddOrEditOption"
						:show-cancel-button="true">
						
							<view class="slot-content" style="width: 90%;margin: 0 auto;font-size: 80%;">
								<text class="lable">选项名：</text>
								<input class="input" type="text" v-model="optionInput.desc"/>
								<view style="margin-top: 20rpx;"></view>
								
								<text class="lable">参考链接：</text>
								<input class="input" type="text" v-model="optionInput.link"/>
								<view style="margin-top: 20rpx;"></view>
								
								<text class="lable">图片链接</text>
									<my-upload-img
									:maxSize="1 * 1024 * 1024"
									:url="uploadUrl"
									@beforeUpload="beforeUploadOptionPic"
									@onUploadSuccess="onUploadOptionPicSuccess">
									</my-upload-img>
								<input class="input" type="text" v-model="optionInput.picSrc"/>
								<image v-if="optionInput.picSrc" mode="aspectFit" style="max-width: 80%" :src="optionInput.picSrc"/>
								<button v-if="optionInput.picSrc" @click="deleteLatestOptionPic" style="color:red">清除</button>
								<view style="margin-top: 20rpx;"></view>
							</view>
						</u-modal>
					</view>
					
					<u-divider color="#fa3534"></u-divider>
					
					<my-empty v-if="post.options.length==0" type="post" text="没有选项"/>
					<view v-for="(option,index) in post.options" :key="option.key">
						<view style="margin-top: 10rpx;font-size: 60%;">选项{{index}}：</view>
						<my-option-show :option="option"/>
						<view v-if="isAdd">
							<button style="display: inline-block;width: 50%;" @click="()=>{toEditOption(index)}">编辑</button>
							<button style="display: inline-block;width: 50%;color:#f99" @click="()=>{removeOption(index)}">删除</button>
						</view>
					</view>
					
				</view>
				
				<view style="height: 60rpx;"/>
				<view style="float:right">
					<button v-if="isAdd" class="confirm-button" @click="doAdd">
						确认发布
					</button>
					<button v-if="!isAdd" class="confirm-button" @click="doUpdate">
						确认修改
					</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		prop:["editId"],
		data() {
			return {
				isAdd:true,//本组件负责帖子的修改或新建，如果是修改，某些项无法修改
				post:{
					id:"",
					title:"",
					content:"",
					picSrc:"",
					options:[
						{
							key:this.util.getTmpKey(),//此为仅前端使用的临时id，与后端设置的id无关
							desc:"A.",
							picSrc:"",
							link:"",
						},
						{//仅测试：
							key:this.util.getTmpKey(),
							desc:"B.",
							picSrc:"",
							link:"",
						}
					],
					endTime:"",
					maxVoteCount:0,
					
					//编辑情况下无法改动上述这些，但下面这些是可以改的
					category:"",
					number:"",
					key:"",
					commentAble:true,
					series:"",
				},
				
				uploadUrl:"",
				
				showEndTime:false,
				timeParams:{
					year: true,
					month: true,
					day: true,
					hour: true,
					minute: true,
					second: false
				},
				
				isSelectCategory:false,
				categoryList:[],
				
				isSelectSeries:false,
				seriesList:[],

				//对选项的新增或编辑 都是在新增帖子时才有的
				isAddOrEditOption:false,
				optionIndex:-1,//为-1表示当前正在新增选项，否则为更新
				optionInput:{//当前正新增或编辑的选项
					key:"-1",//仅前端使用的临时id，与后端设置的id无关
					desc:"1",
					picSrc:"",
					link:"1",
				},
				
				refreshCallback:null,//添加完成后的回调
			}
		},
		methods:{
			openSelectCategory(){
				this.categoryList = [{label:"不指定",value:""}]
				for(const cate of this.constant.categories){
					this.categoryList.push({label:cate,value:cate})
				}
				this.isSelectCategory=true
			},
			confirmCategory(option){
				this.post.category = option[0].value
			},
			confirmEndTime(time){
				this.post.endTime = time.year+"-"+time.month+"-"+time.day+" "+time.hour+":"+time.minute
			},
			openSelectSeries(){
				this.api.post.getAllSelfSeries((data)=>{
					const seriesList = data.seriesList
					if(seriesList.length==0){
						this.util.toast("当前还没有定义系列名")
						return
					}

					this.seriesList = [{text:"不设置"}]
					for(const series of seriesList)
						this.seriesList.push({text:series})
					this.isSelectSeries=true
				})
			},
			confirmSeries(index){
				if(index==0)
					this.post.series = ""
				else
					this.post.series = this.seriesList[index].text;
			},
			toAddOption(){
				for(const key in this.optionInput){
					this.optionInput[key]=""
				}
				this.optionIndex=-1
				this.isAddOrEditOption=true
			},
			toEditOption(index){
				const targetOption = this.post.options[index]
				for(const key in targetOption){
					this.optionInput[key]=targetOption[key]
				}
				this.optionIndex=index
				this.isAddOrEditOption=true
			},
			removeOption(index){
				this.deleteTargetPic(this.post.options[index].picSrc)
				this.post.options.splice(index,1)
			},
			confirmAddOrEditOption(){
				this.isAddOrEditOption=true
				if(!this.optionInput.desc){
					this.util.toast("选项描述不能为空")
					return
				}
				if(this.optionInput.desc.length>36){
					this.util.toast("选项描述过长")
					return
				}
				
				//新增的话就给一个新key，否则保持原来的key
				this.optionInput.key = this.optionIndex<0?this.util.getTmpKey():this.optionInput.key
					
				if(this.optionIndex<0)
					this.post.options.push({...this.optionInput})
				else
					this.post.options.splice(this.optionIndex,1,{...this.optionInput})
				this.isAddOrEditOption=false
			},
			cancelAddOrEditOption(){
				this.deleteLatestOptionPic()
				this.isAddOrEditOption=false
			},
			
			doAdd(){
				if(!this.post.title){
					this.util.toast("标题不能为空")
					return
				}
				if(!this.post.number){
					this.util.toast("编号不能为空")
					return
				}
				if(this.post.options.length<=1){
					this.util.toast("选项数至少为2")
					return
				}
				if(this.post.options.length>16){
					this.util.toast("选项数最多16")
					return
				}
				
				this.api.post.add(this.post,(data)=>{
					//此处置为空避免触发冗余回收机制
					this.post.picSrc=null
					this.optionInput.picSrc=null
					this.util.routeBack()
					this.util.toast("添加成功")
				})
			},
			doUpdate(){
				if(!this.post.number){
					this.util.toast("编号不能为空")
					return
				}
				
				this.api.post.update(this.post,(data)=>{
					this.util.routeBack()
					this.util.toast("修改成功")
				})
			},

			//关于上传相关细节，参考SelfDetails.vue
			beforeUploadPostPic(data,callback){
				callback(true)
			},
			onUploadPostPicSuccess(resultData){
				this.deleteLatestPostPic()
				this.post.picSrc = resultData.url
			},
			deleteLatestPostPic(){//清理的原因是避免冗余
				if(this.deleteTargetPic(this.post.picSrc))
					this.post.picSrc=null
			},
			//-----上面是针对Post的图，下面是针对每个Option的图
			beforeUploadOptionPic(data,callback){
				callback(true)
			},
			onUploadOptionPicSuccess(resultData){
				this.deleteLatestOptionPic()
				this.optionInput.picSrc = resultData.url
			},
			deleteLatestOptionPic(){
				if(this.deleteTargetPic(this.optionInput.picSrc))
					this.optionInput.picSrc=null
			},
			deleteTargetPic(picSrc){
				if(picSrc){
					const names = picSrc.split('/')
					this.api.post.deletePicture(names[names.length-1])
					return true
				}
				return false
			},
		},
		onLoad(routeParamObj){
			this.uploadUrl = this.api.post.getUploadPictureUrl()
			
			if(routeParamObj&&routeParamObj.editId){
				this.api.post.getById(routeParamObj.editId,routeParamObj.editKey,(data)=>{
					this.post=data
					this.isAdd=false
				})
			}else{
				this.post.number=this.util.getRandomStr(8).toUpperCase()
			}
		},
		onUnload() {
			//路由返回会导致页面的卸载
			if(this.isAdd){
				//如果是添加成功后的返回，记得先赋值连接为null，避免此处误删
				this.deleteLatestPostPic()
				this.deleteLatestOptionPic()
			}
			//编辑帖子的情况下，无法改动pic，所以无需删除
		},
	}
</script>

<style lang="scss">

@import "@/common/my-class.scss";
@import "@/pages/Self/_common.scss";

</style>
