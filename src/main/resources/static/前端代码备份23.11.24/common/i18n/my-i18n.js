//暂时自定义处理i18n

//做懒加载失败了，所以打算直接引入使用
import obj_zh_CN from '@/common/i18n/language/zh_CN.js'
import obj_en_US from '@/common/i18n/language/en_US.js'

export default{
	install(Vue){
		
		const vuex = Vue.prototype.vuex
		const constant = Vue.prototype.constant
		const util = Vue.prototype.util
		
		function getLangObjByLangType(langType){
			if(langType==constant.LangType.zh_CN){
				return obj_zh_CN
			}
			if(langType==constant.LangType.en_US){
				return obj_en_US
			}
		}
		
		const getCurrentLanguage=()=>{
			return vuex.get("lang")
		}
		
		Vue.prototype.i18n={
			parse(key){
				const data = getLangObjByLangType(getCurrentLanguage())
				return data[key]
			},
			getCurrentLanguage(lang){
				return getCurrentLanguage()
			},
			changeLanguage(lang){
				vuex.set("lang",lang)
				
				const tabBarTextKeys =  constant.tabBarTextKeys
				for (var i = 0; i < tabBarTextKeys.length; i++) {
					util.setTabBarText(i, Vue.prototype.i18n.parse(tabBarTextKeys[i]))
				} 
				
				//测试发现不发送事件也成功改变页面语言，所以不再需要事件处理，可能是因为上面vuex的数据改变自动触发了页面刷新
				// uni.$emit("i18n-change")//发送语言改变事件
			}
		}
		
		
		//让每个组件都能监听到语言改变事件并做出响应
		// Vue.mixin({
		// 	onLoad() {
		// 		uni.$on('i18n-change',function(){
		//    　　 	this.$forceUpdate()
		// 		});
		// 	},
			
		// 	onUnload() {
		// 		uni.$off('i18n-change');
		// 	}
		// })
	}
}
