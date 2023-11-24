//部分页面共用的js函数或数据放在这些页面的上一级目录里
const validator = {
	username(value,vm){//参数vm可能以后有用，比如i18n
		if(!value||value.length<3) return "至少3位"
		if(value.length>20) return "过长"
		return ""
	},
	password(value,vm){
		if(!value||value.length<4) return "至少4位"
		if(value.length>36) return "过长"
		return ""
	},
	securityCode(value,vm){
		if(!value) return "不能为空"
		if(value.length>8) return "过长"
		return ""
	},
	email(value,vm){
		const emailReg=/^[a-zA-Z0-9_-]+@([a-zA-Z0-9]+\.)+(com|cn|net|org)$/;
		if(!value||!emailReg.test(value)) return "邮箱格式不正确"
		return ""
	},

	//具体用法可参考Login.vue
	validateAll(formData,validateFunc){
		let passAll = true
		for(const item in formData){
			const result = validateFunc(item,formData[item].value)
			formData[item].validate = result
			if(result) passAll=false
		}
		return passAll
	},
}

export default {
	validator
}