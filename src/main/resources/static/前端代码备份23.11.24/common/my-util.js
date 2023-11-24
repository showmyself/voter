//全局工具函数

const pagesObj = require('@/pages.json')//拿到pages.json，此处直接就把json转换成了obj，不过注意多附带了一层default

export default{
	install(Vue){
		let $u=Vue.prototype.$u
		const {storageKey}=Vue.prototype.constant
		const findRoutePathByName=(name)=>{//pages里配置的每个page需要多给一个name，方便根据名字进行路由
			const target = pagesObj.default.pages.find((item)=>{
				if(item.name==name)
					return true
				return false
			})
			return target.path
		}

		Vue.prototype.util={
			fn1(){console.log("fn1")},
			
			setTabBarText(index,text){
				uni.setTabBarItem({
					  index: index,
					  text: text//还可以设置其他细节，网搜，此处略
				})
			},

			routeTo(name,paramsObj){
				const path = findRoutePathByName(name)
				if(path)
					$u.route(path,paramsObj)//onLoad周期形参接收
				else console.log("无效的路由")
			},
			
			//使用自定义的routeBack的话，会自动触发返回目标页面的backRefresh方法
			routeBack(){
				$u.route({ type: 'navigateBack', delta: 1})
				
				const pages = getCurrentPages()
				if(pages.length>0){
					const prevVm = pages[pages.length-2]?.$vm//实测发现，虽然上面已经做了返回，但最顶级的页面还没有消除，所以取倒数第二个
					if(prevVm&&prevVm.backRefresh)
						prevVm.backRefresh()
				}
			},
			
			routeRedirect(name){
				const path = findRoutePathByName(name)
				if(path)
					$u.route({ type: 'redirectTo', url: path })
				else console.log("无效的路由")
			},
			
			routeTab(name){
				const path = findRoutePathByName(name)
				if(path)
					uni.switchTab({url: path})
				else console.log("无效的路由")
			},
			
			toast(str,duration){
				if(!duration)duration=1500
				$u.toast(str,duration)
			},
			
			showLoading(info){
				uni.showLoading({title: info})
			},
			hideLoading(){
				uni.hideLoading();
			},
								
			//从本地读
			readFromStorage(key,defaultVal){
				let lifeData = uni.getStorageSync(storageKey);
				if(!lifeData||!lifeData[key]) return defaultVal
				return lifeData[key]
			},
			
			//写入本地
			setToStorage(key,value){
				let tmp = uni.getStorageSync(storageKey);
				tmp = tmp ? tmp : {};
				tmp[key] = value;
				uni.setStorageSync(storageKey, tmp);
			},
			
			//根据timestamp获取日期字符串
			getDateStrByTimestamp(timeStamp){
				let Dates = new Date(parseInt(timeStamp));
				let Y = Dates.getFullYear();
				let M = Dates.getMonth() + 1;
				let D = Dates.getDate();
				return Y + (M < 10 ? "-0" : "-") + M + (D < 10 ? "-0" : "-") + D;
			},
				
			//获取当前时间的字符串
			getPresentTimeStr(){
				//给Date添加日期时间格式化函数
				Date.prototype.format = function (fmt) {
					let o = {
						"M+": this.getMonth() + 1,                 //月份
						"d+": this.getDate(),                    //日
						"h+": this.getHours(),                   //小时
						"m+": this.getMinutes(),                 //分
						"s+": this.getSeconds(),                 //秒
						"q+": Math.floor((this.getMonth() + 3) / 3), //季度
						"S": this.getMilliseconds()             //毫秒
					};
					if (/(y+)/.test(fmt)) {
						fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
					}
					for (let k in o) {
						if (new RegExp("(" + k + ")").test(fmt)) {
							fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
						}
					}
					return fmt;
				}
				return new Date().format("yyyy-MM-dd hh:mm:ss")
			},
			
			removeSecondForTimeStr(timeStr){
				let array = timeStr.split(":")
				return array[0]+":"+array[1]
			},
			
			//增加全局临时数据（vuex里的数据一般是可以显示到页面做响应的数据，否则一般也不需要用vuex）
			setTmpData(name,value){
				if(Vue.prototype.tmp){
					Vue.prototype.tmp[name]=value
				}
				else{
					Vue.prototype.tmp={}
					Vue.prototype.tmp[name]=value
				}
			},
			
			//获取全局临时数据
			getTmpData(name,defaultVal){
				if(Vue.prototype.tmp){
					const val = Vue.prototype.tmp[name]
					if(!val) return defaultVal
					return val
				}
				else return defaultVal
			},
			
			//生成指定长度的随机串，第二个参数charsStr代入生成的随机串中能包含的所有字符
			getRandomStr(length, charsStr) {
				if(!charsStr) charsStr='0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'
			    let result = '';
			    for (let i = length; i > 0; --i) result += charsStr[Math.floor(Math.random() * charsStr.length)];
			    return result;
			},
			
			//获得一个临时的暂时全局唯一的id
			getTmpId(){
				if(!Vue.prototype.tmpId)
					Vue.prototype.tmpId = 0
				else
					Vue.prototype.tmpId += 1
				return Vue.prototype.tmpId.toString()
			},
			//获得一个临时的暂时全局唯一的key
			getTmpKey(){
				if(!Vue.prototype.tmpKey)
					Vue.prototype.tmpKey = -1
				else
					Vue.prototype.tmpKey -= 1
				return Vue.prototype.tmpKey.toString()
			},
			
			
			//全局事件相关
			addEventHandler(eventName,handler){
				uni.$on(eventName,handler)
			},
			broadcastEvent(eventName,data){
				uni.$emit(eventName,data)
			},
			//不知为何，之前测试发现先remove紧接着再add，handler失效了
			removeEventHandler(eventName,handler){
				uni.$off(eventName,handler)
			},
			
			//根据pageSize和total，计算出总页数，注意最小页数设为1即使没有任何数据
			getMaxPageBy(pageSize,total){
				let maxPage = Math.floor(total/pageSize)
				if(total%pageSize>0)maxPage+=1
				if(maxPage<1)maxPage=1
				return maxPage
			},
			
			//配合my-pagination的searchPage函数内在api获取到total后调用：（可参考Visit页面）
			dealPage(targetPage,pageSize,total){
				const thisUtil = Vue.prototype.util
				
				const maxPage = thisUtil.getMaxPageBy(pageSize,total)
				
				if(targetPage>maxPage){
					thisUtil.toast("当前已至最大页数："+maxPage+"，没有更多了")
					return false
				}

				if(targetPage>1)
					thisUtil.toast("当前已至页数："+targetPage,500)
				return true
			}
		}
	}
}
