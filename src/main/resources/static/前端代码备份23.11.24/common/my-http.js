//http请求响应配置
export default {
	install(Vue) {
		
		const $u = Vue.prototype.$u
		const util = Vue.prototype.util
		const store = Vue.prototype.store
		const baseUrl = Vue.prototype.constant.baseUrl
		
		$u.http.setConfig({
			baseUrl: baseUrl,
			method: 'POST',
			dataType: 'json',
			showLoading: true, //是否显示请求中的loading
			loadingText: '请求中...', //请求loading中的文字提示
			loadingTime: 800, //在此时间内，请求还没回来的话，就显示加载中动画，单位ms
			originalData: true, //是否在拦截器中返回服务端的原始数据
			loadingMask: true, //展示loading的时候，是否给一个透明的蒙层，防止触摸穿透
			header: {
				'content-type': 'application/json;charset=UTF-8'
			},
		});
		
		// 请求拦截，配置Token等参数
		$u.http.interceptor.request = (config) => {
			
			config.header.token = store.state.token;
			
			// return false; // 取消此次请求
		}
		
		// 响应拦截，判断状态码是否通过
		$u.http.interceptor.response = (res) => {
			const statusCode = res.statusCode
			if(statusCode==200){
				const result = res.data
				if(result.flag)
					return result.data
				else
					return Promise.reject(result.msg);//到达catch
			}
			else{
				util.toast(statusCode+" : "+res.data?.error)
				return false
			}
		}
	}
}
