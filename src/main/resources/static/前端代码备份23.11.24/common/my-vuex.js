import Vuex from 'vuex'
export default {
	install(Vue){
		Vue.use(Vuex)
		
		const {readFromStorage,setToStorage} = Vue.prototype.util
		const {localKeys,LangType} = Vue.prototype.constant
		
		//给出默认值
		const defaultState={
			isLogin:false,
			user:{id:"",username:"",age:"",sex:""},
			token:"",
			lang:LangType.zh_CN,
			timeCount:0,
			test:"vuex test prop!"
		}
		
		//localKeys里的，自动从本地赋值一下
		const startState={}
		for(const key in defaultState){
			if(localKeys.indexOf(key)>=0)
				startState[key] = readFromStorage(key,defaultState[key])
			else startState[key] = defaultState[key]
		}
		
		let store = new Vuex.Store({
			state: {
				...startState
			},
			mutations: {
				save(state, payload) {
					//payload赋值：{name:...,value:...}
					//name可以赋值比如user.address.city，则自动对应给state.user.address.city赋值，对此下面做如下处理
					let names = payload.name.split('.');
					let len = names.length;
					
					if(len<2)
						state[payload.name] = payload.value;
					else
					{
						let obj = state;
						for(let i = 0; i < len - 1; i ++) {
							obj = obj[names[i]];//注意state里的对象型变量一开始就应该包含完整的结构，否则此处就会出错
						}
						obj[names[len - 1]] = payload.value;
					}
					
					
					//---------------------------------------------------------------------------------------
					//判断变量名是否存在于localKeys，如果存在则同时保存到本地
					let saveKey = len >= 2? names[0] : payload.name;
					if(localKeys.indexOf(saveKey) != -1) 
						setToStorage(saveKey,state[saveKey])
				}
			}
		})
		
		//-------------------------------------------------------------------------------------------------
		Vue.prototype.store=store
		Vue.prototype.vuex={

			//读
			get(name){
				return store.state[name]
			},

			//写
			set(name, value){
				store.commit('save', {
					name:name,value:value
				})
			},
			
			//还原默认值
			setDefault(name){
				store.commit('save',{
					name:name,value:defaultState[name]
				})
			}

		}
	}
}
