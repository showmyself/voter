//利用js函数存储一些全局需要的根据参数变化而变化的样式
export default{
	install(Vue){
		const constant=Vue.prototype.constant
		
		Vue.prototype.st={
			
			//赋值:style时，均加上中括号！在其内添加下面的函数即可，这样既能合并，也能防止小程序端编译错误
			
			//比如
			marginLeft(length){
				return{
					display:"inline-block",
					"margin-left":length
				}
			},
			icon(){
				return{
					width:Math.floor(constant.rootFontSize*1.2)+"rpx"
				}
			}
		}
	}
}
