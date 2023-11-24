<template>
	<view class="my-page-root">
		<my-nav-bar title="帖子详情" />
		<view class="my-page-root-child">

			<!--系列、收藏、删除-->
			<view class="my-flex-usual">

				<view class="my-center-one" style="width: 20%;height: 35rpx;">
					<a class="series-btn"
						@click="()=>{if(post.series)findSameSeries()}">系列：{{post.series?post.series:"无"}}</a>
				</view>

				<view style="width: 40%" />
				<view v-if="post.userId!=userId" style="width: 20%" />

				<view class="my-center-one" style="width: 20%;">
					<a class="collect-btn" @click="collect">{{hasCollected?"★已收藏":"✰未收藏"}}</a>
				</view>
				
				<!--如果浏览的是自己的，则可以做删除操作-->
				<view v-if="post.userId==userId" class="my-center-one" style="width: 20%;">
					<image mode="widthFix" class="icon" :style="[st.icon()]" :src="require('@/static/删除.png')" @click="isDelete=true"/>
					
					<u-modal v-model="isDelete" :title-style="{color: 'cornflowerblue'}" @confirm="confirmDelete"
						@cancel="isDelete=false" :show-cancel-button="true">
						<view class="my-center-one" style="height: 80rpx; color:red">确认删除此帖？</view>
					</u-modal>
				</view>

			</view>

			<view style="height: 1px;background-color: lightgray;" />

			<view style="margin-top: 15rpx;" />
			<view class="title" style="width: 65%;">{{post.title}}</view>
			<view style="margin-top: 30rpx;" />
			<view class="content">{{post.content}}</view>
			<view style="margin-top: 30rpx;" />
			<image v-if="post.picSrc" mode="aspectFit" style="max-width: 90%" :src="post.picSrc" @error="imageError"/>
			<!-- <image v-if="post.picSrc" mode="aspectFit" style="max-width: 90%" src="http://192.168.56.1:9000/static/pictures/akeron-1694774274642.png" @error="imageError"/> -->
			<!-- <view :style="{backgroundImage:`url(${post.picSrc})`,backgroundSize: 'cover'}"/> -->
			<view style="margin-top: 30rpx;" />

			<view style="margin-top: 20rpx;" />
			<view>
				<view :class="post.finished?'finished':'not-finished'" space="ensp" style="display: inline-block;">
					{{post.finished?"◉ 已结束  ":"● 进行中...  "}}</view>
				<a style="color:slategray;float:right" @click="isShowMoreDetail=!isShowMoreDetail">[查看细节]</a>
			</view>
			<view style="margin-top: 15rpx;" />

			<view v-if="isShowMoreDetail">

				<view>
					<text class="detail-label">
						编号：
					</text>
					<text class="detail-value">
						{{post.number}}
					</text>
				</view>

				<view>
					<text class="detail-label">
						浏览量：
					</text>
					<text class="detail-value">
						{{post.viewCount}}
					</text>
				</view>

				<view>
					<text class="detail-label">
						创建时间：
					</text>
					<text class="detail-value">
						{{util.removeSecondForTimeStr(post.startTime)}}
					</text>
				</view>

				<view>
					<text class="detail-label">
						截止日期：
					</text>
					<text class="detail-value">
						{{post.endTime?util.removeSecondForTimeStr(post.endTime):"无期限"}}
					</text>
				</view>

				<view>
					<text class="detail-label">
						限票：
					</text>
					<text class="detail-value">
						{{post.maxVoteCount>0?post.maxVoteCount:"不限制"}}
					</text>
				</view>

			</view>

			<view style="height: 1px;background-color: lightgray;" />

			<view style="margin-top: 30rpx;" />
			<view v-if="!isLogin" class="vote-desc">
				尚未登录，投票需先登录
			</view>
			<view v-if="isLogin" style="margin-top: 3rpx;" class="vote-desc">
				{{voteOptionId?"您已选投了 ["+getVoteOptionDesc()+"]":"您尚未参与选投（点击选项进行选投）"}}
			</view>

			<view style="margin-top: 30rpx;" />

			<!--选择列表-->
			<view>
				<view v-for="option,index in post.options" :key="index">
					<view>
						<view style="display:inline-block; font-size:70%;width: 10%;">选项{{index}}：</view>
						<view style="display:inline-block;min-height: 50rpx;width: 90%;" @click="()=>{voteFor(index)}">
							<my-option-show :option="option" />
						</view>
						<view style="margin-top: 20rpx;" />
					</view>
				</view>
			</view>

			<view style="height: 200rpx;" />
			
			<!--悬浮的工具栏，包括：展示结果按钮、切换视图按钮、评论按钮-->
			<view class="toolbar my-center-one">
				<view style="width: 90%;">
					<view style="display: inline-block; width: 20%;margin-left: 1%;">
						<button @click="toShowResult()">{{showChart?"隐藏结果":"展示结果"}}</button>
					</view>

					<button style="width: 30%;float:right;margin-right: 1%;" @click="showComments">
						<image mode="widthFix" class="icon" :style="[st.icon()]" :src="require('@/static/评论.png')" />
						<text v-if="post.commentAble">评论({{post.commentCount}})</text>
						<text v-if="!post.commentAble">评论已被关闭</text>
					</button>
				</view>
			</view>
			
			<!--弹出层展示图-->
			<u-popup v-model="showChart" mode="bottom" height="50%">
				
				<view style="height: 30rpx;"/>
				<view style="width: 20%;" class="my-center-one my-at-center-horizontal-block">
					<a v-if="showChart" @click="changeChart" style="color:darkgray;font-size: 110%;font-weight: 500;">切换视图</a>
				</view>
				<view style="height: 30rpx;"/>
				
				<!--展示图-->
				<view class="my-center-one" v-if="loaded&&showChart">
					<view v-if="chartType=='bar'" style="width: 90%;">
						<!--自己做的条形展示图-->
						<my-bar-chart :options="myBarChartData"></my-bar-chart>
					</view>
					<view v-if="chartType=='pie'" style="width: 90%;">
						<!--引用别人的插件展示扇形图（饼图）-->
						<qiun-data-charts type="pie" :opts="pieChartData.opts" :chartData="pieChartData.chartData" />
					</view>
				</view>

			</u-popup>

			<!--弹出层评论-->
			<u-popup v-model="isShowComments" mode="bottom" height="80%">
				<view>
					<view class="my-center-one" style="color:gray;font-weight: bold">评论</view>
					<view style="height: 20rpx;" />
					<view class="my-flex-usual" style="align-items: center;">
						<view style="width: 20%;">
							<a style="text-decoration: underline;font-size: 70%;"
								@click="orderComments">{{"当前："+(orderByTimeOrHot=="hot"?"按热度排序":"按时间排序")}}</a>
						</view>
						<view style="width: 10%;" />
						<view style="width: 50%;">
							<input style="background-color: lightgray;height: 30rpx;border-radius: 10rpx;"
								v-model="inputComment" />
						</view>
						<view style="width: 2%;" />
						<view style="width: 18%;">
							<button style="font-size: 70%;width: 80%;float:left" @click="addComment">发送</button>
						</view>
					</view>
					<view style="height: 20rpx;" />

					<my-empty v-if="comments.length==0" type="comment" text="评论为空" />

					<view v-for="cmt,index in comments" :key="cmt.id">
						<view style="margin-top: 20rpx;" />
						<!--不知为何，这里多传入一个cmt才正确-->
						<view @click="()=>{toReply(index,cmt)}">
							<my-comment :comment="cmt" />
						</view>
						<view v-if="isLogin">
							<a style="text-decoration: underline;color: red;font-size: 80%" v-if="userId==cmt.userId"
								@click="()=>{isDeleteComment=true;targetCommentIndex=index}">删除</a>
						</view>
					</view>

					<u-modal v-model="isReply" title="回复" :title-style="{color: 'cornflowerblue'}"
						@confirm="confirmReply" @cancel="isReply=false" :show-cancel-button="true">
						<view class="slot-content">
							<view style="margin-top: 20rpx;" />
							<view style="width: 80%;margin:0 auto;">
								<input style="background-color: lightgray;border-radius: 8rpx;"
									v-model="commentForReply">
							</view>
							<view style="margin-top: 20rpx;" />
						</view>
					</u-modal>

					<u-modal v-model="isDeleteComment" :title-style="{color: 'cornflowerblue'}"
						@confirm="confirmDeleteComment" @cancel="isDeleteComment=false" :show-cancel-button="true">
						<view style="color:red" class="my-center-one">
							确定删除？
						</view>
					</u-modal>

					<my-pagination @searchPage="searchPage" ref="mypagination" />
				</view>
			</u-popup>

		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				post: {
					id: "",
					title: "",
					content: "",
					picSrc: "",
					category: "",
					number: "",
					key: "",
					startTime: "",
					endTime: "",
					viewCount: 0,
					userId: 1,
					commentAble: true,
					maxVoteCount: 1,
					voteCount: 0, //当前已投票数
					series: "",
					options: [],
					finished: false,
					commentCount: 0,
				},

				isShowMoreDetail: false,

				voteOptionId: null, //用户已经投过的选项id，没有投过则为null
				showChart: false, //是否显示图形，如果voteOptionId为空，则默认不显示
				chartType: 'bar', //柱形'bar'或者扇形fan
				loaded: false, //加载完毕再允许显示图形，主要针对自己的组件bug

				//自己做的条形图所需数据
				myBarChartData: [{
					desc: "",
					count: 0
				}, ],
				//引入的扇形图所需数据
				pieChartData: {
					chartData: {
						series: [{ //扇形图有一定的局限，如果选项描述过长，可能显示不友好，自己没法调整，只有让帖子建立者自行调整选项长度
							data: [{
								name: "",
								value: 0,
								labelText: "自定义"
							}, ]
						}]
					},
					opts: { //对显示图形的细节调整
						extra: {
							pie: {
								border: true,
								borderWidth: 1,
								borderColor: "white",
								//..其他属性暂时不需要
							},
						}
					},
				},

				isDelete: false, //正要删除
				hasCollected: false, //用户是否已收藏过
				isLogin: false, //用户是否已登录
				userId: null, //登录的用户id

				isShowComments: false,
				comments: [],
				pageSize: 3,
				orderByTimeOrHot: "time",
				inputComment: "",
				isReply: false,
				commentForReply: "",
				isDeleteComment: false,
				targetCommentIndex: 0,
			};
		},
		methods: {
			imageError(e){
				console.log("加载图片发生错误："+e.detail.errMsg)
			},
			toShowResult(){
				this.showChart=!this.showChart
				if(this.showChart)
					this.scrollToChart()
			},
			changeChart() {
				this.chartType = this.chartType == 'bar' ? 'pie' : 'bar'
				this.scrollToChart()
			},
			scrollToChart(){//不再需要，图形将在popup窗口里展示
				//页面滑动到最底部，需要一点延迟把元素渲染出来后再处理才正确：
				// setTimeout(()=>{
				// 	uni.pageScrollTo({scrollTop: 99999, duration: 200});
				// },200)
			},
			voteFor(index) {
				if (!this.isLogin) {
					this.util.toast("登录后再选投")
					return
				}
				if (this.voteOptionId) {
					this.util.toast("已经投过票了")
					return
				}

				const optionId = this.post.options[index].id
				this.loaded = false
				this.api.post.vote(this.post.id, optionId, () => {

					this.util.toast("投选成功")

					this.post.voteCount += 1
					this.voteOptionId = optionId

					this.post.options[index].count += 1
					this.refreshChartDataByPostOptions()

					this.loaded = true
					this.showChart = true
				})

			},
			getVoteOptionDesc() {
				for (const option of this.post.options) {
					if (option.id == this.voteOptionId)
						return option.desc
				}
				return "空"
			},
			refreshChartDataByPostOptions() {

				this.myBarChartData = [...this.post.options]

				let pirArray = []
				for (const option of this.post.options) {
					let rate
					if (this.post.voteCount == 0)
						rate = Math.floor(100 * 1 / this.post.options.length) + "% "
					else rate = Math.floor(100 * option.count / this.post.voteCount) + "% "

					pirArray.push({
						name: option.desc,
						value: option.count,
						labelText: rate + option.desc
					})
				}
				this.pieChartData.chartData.series[0].data = [...pirArray]
			},
			collect() {
				if (!this.hasCollected) {
					this.api.user.addCollect(this.post.id, (data) => {
						this.util.toast("收藏成功", 800)
						this.hasCollected = true
					})
				} else {
					this.api.user.removeCollect(this.post.id, (data) => {
						this.util.toast("取消了收藏", 500)
						this.hasCollected = false
					})
				}
			},
			findSameSeries() {
				this.util.routeTo("SameSeriesPosts", {
					userId: this.post.userId,
					series: this.post.series,
				})
			},

			confirmDelete() {
				this.api.post.delete(this.post.id, () => {
					this.util.routeBack()
					this.util.toast("已删除！")
				})
			},

			load() {
				//post的id和key会被预先从路由参数里获取并赋值
				this.loaded = false
				this.api.post.getById(this.post.id, this.post.key, (data) => {
					this.post = data
					this.refreshChartDataByPostOptions()
					this.loaded = true
				}, (msg) => {
					this.util.routeBack()
					this.util.toast(msg ? msg : "请求失败")
				})
			},
			backRefresh() { //该函数自动在使用util.routeBack到该页面后被调用
				this.load()
			},


			searchPage({targetPage,newStart}) {
				this.api.comment.findPostComments(targetPage, this.pageSize, this.post.id, this.orderByTimeOrHot, (
				data) => {
					const succ = this.util.dealPage(targetPage, this.pageSize, data.total)
					if (succ) {
						if (newStart)
							this.comments = data.comments
						else
							this.comments = [...this.comments, ...data.comments]
					}
				})
			},
			toPage1() {
				if (this.$refs.mypagination)
					this.$refs.mypagination.reset()
			},
			showComments() {
				if (this.post.commentAble) {
					this.isShowComments = true
					setTimeout(()=>{//延迟执行以解决一个无效的bug
						this.toPage1()
					},100)
				}
			},
			orderComments() {
				this.orderByTimeOrHot = this.orderByTimeOrHot == "hot" ? "time" : "hot"
				this.showComments()
			},
			addComment() {
				if (this.inputComment.length < 3) {
					this.util.toast("评论字数太少")
					return
				}

				const dto = {
					id: null,
					postId: this.post.id,
					remark: this.inputComment
				}

				this.api.comment.add(dto, (data) => {
					this.util.toast("成功添加一条评论")
					this.post.commentCount += 1
					this.showComments()
				})
			},
			confirmDeleteComment() {
				this.api.comment.delete(this.comments[this.targetCommentIndex].id, (succ) => {
					this.util.toast("已删除")
					this.post.commentCount -= 1
					this.showComments()
				})
			},
			toReply(index, cmt) {
				this.isReply = true;
				this.targetCommentIndex = index;
			},
			confirmReply() {
				if (this.commentForReply.length == 0) {
					this.util.toast("回复不能为空")
					return
				}

				const dto = {
					id: this.comments[this.targetCommentIndex].id,
					postId: this.post.id,
					remark: this.commentForReply
				}

				this.api.comment.reply(dto, (data) => {
					this.util.toast("回复成功")
					this.post.commentCount += 1
					this.showComments()
				})
			}
		},
		onLoad(routeParamObj) {
			const {
				id,
				key
			} = routeParamObj
			this.post.id = id
			this.post.key = key //这个是安全的，通过一般搜索无法搜索到带key的，只有搜自己的会自动附上key
			this.load()

			this.isLogin = this.vuex.get("isLogin")
			if (this.isLogin) {
				this.userId = this.vuex.get("user").id
			}

			if (this.isLogin) {
				this.api.post.hasVoted(id, (data) => {
					this.voteOptionId = data.optionId
					// if (this.voteOptionId)
					// 	this.showChart = true
					// else this.showChart = false
				})

				this.api.user.hasCollected(id, (data) => {
					this.hasCollected = data
				})
			} else this.showChart = false
		}
	}
</script>

<style lang="scss" scoped>
	.title {
		color: royalblue;
		font-weight: 650;
		font-size: 120%;
	}

	.content {
		color: dimgray;
		font-weight: 600;
		font-size: 80%;
		white-space: pre;
	}

	.number {
		color: black;
		font-weight: 400;
		font-size: 60%;
	}

	.series-label {
		color: black;
		font-weight: 400;
		font-size: 60%;
	}

	.series-btn {
		display: inline-block;
		// color:darkorange;
		font-weight: 500;
		font-size: 80%;
		text-decoration: underline;
	}

	.view-count {
		color: black;
		font-weight: 400;
		font-size: 60%;
	}

	.detail-label {
		color: black;
		font-weight: 300;
		font-size: 70%;
	}

	.detail-value {
		// color:blue;
		font-weight: 500;
		font-size: 70%;
	}

	.not-finished {
		color: seagreen;
		font-weight: 600;
		font-size: 80%;
	}

	.finished {
		color: indianred;
		font-weight: 600;
		font-size: 80%;
	}

	.comment-btn {
		// color:dimgray;
		font-weight: 550;
		font-size: 90%;
		width: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.collect-btn {
		// color:orange;
		font-weight: 500;
		font-size: 70%;
		text-decoration: underline;
	}

	.delete-btn {
		color: red;
		font-weight: 500;
		font-size: 70%;
		text-decoration: underline;
	}

	.vote-desc {
		color: dimgray;
		font-weight: 600;
		font-size: 110%;
	}

	.show-chart-btn {
		color: black;
		font-weight: 600;
		font-size: 100%;
		text-decoration: underline;
	}

	.change-chart-btn {
		color: blue;
		font-weight: 500;
		font-size: 100%;
		text-decoration: underline;
	}

	.toolbar {
		position: fixed;
		left: 0;
		right: 0;
		bottom: var(--window-bottom);
		z-index: 1;
		height: 70rpx;
		background-color: ghostwhite;
		border: solid 1px gray;
	}
</style>