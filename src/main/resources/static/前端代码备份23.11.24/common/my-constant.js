//全局常量
export default{
	install(Vue){
		Vue.prototype.constant={
			
			//语言类型
			LangType:{
					zh_CN:"zh_CN",
					en_US:"en_US"
			},
			
			//存储到本地的对象的key
			storageKey:"voter-local",
			
			//此列表里赋值vuex.state中存在的某些变量名，设定当这些变量被改变时 也同步到本地存储中
			localKeys:['token','lang', 'timeCount'],
			
			//tabBar的词key，对i18n有用
			tabBarTextKeys:['recommend','search','self'],
			
			//html里无法引用css值，所以这里再写一个拿来引用
			rootFontSize:25,
			
			categories:["生活","食品","游戏","电影","书籍"],
			
			//请求域名，对于H5端已在manifest.json中配置了代理，此处不再需要配
			// #ifdef H5
			baseUrl:"",
			// #endif
			// #ifdef MP-WEIXIN || APP-PLUS
			baseUrl:"http://192.168.101.14:9000",//经实测，末尾不要加/
			// #endif
		}
	}
}
