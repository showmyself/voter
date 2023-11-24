//存储api，简单处理直接写在这里，复杂处理则代入处理函数
export default{
	install(Vue){
		let $u=Vue.prototype.$u
		const util=Vue.prototype.util
		const baseUrl=Vue.prototype.constant.baseUrl
		
		const usuallyGet=(url,succ,fail)=>{
			$u.get(url).then((data)=>{
				if(succ)
					succ(data)
			}).catch((msg)=>{
				if(fail)
					fail(msg)
				else
					util.toast(msg?msg:"请求失败")
			})
		}
		const usuallyPost=(url,dto,succ,fail)=>{
			$u.post(url,dto).then((data)=>{
				if(succ)
					succ(data)
			}).catch((msg)=>{
				if(fail)
					fail(msg)
				else
					util.toast(msg?msg:"请求失败")
			})
		}
		
		Vue.prototype.api={
			user:{
				getSelf(succ){//此处返回的是promise对象
					const url= "/user/getSelf" //注意如果使用$u.get或$u.post，则不用拼接baseUrl，因为my-http里已配置过
					usuallyGet(url,succ)
				},
				login(dto,succ){
					const url = "/user/login"
					usuallyPost(url,dto,succ)
				},
				register(dto,succ){
					const url = "/user/register"
					usuallyPost(url,dto,succ)
				},
				forgetPassword(dto,succ){
					const url = "/user/forgetPassword"
					usuallyPost(url,dto,succ)
				},
				changePassword(dto,succ){
					const url = "/user/changePassword"
					usuallyPost(url,dto,succ)
				},
				sendValidCodeToEmail(usernameOrEmail){
					const url = "/user/sendValidCodeToEmail?usernameOrEmail="+usernameOrEmail
					$u.get(url).then((data)=>{
						util.toast("发送成功 ！")
					})
				},
				
				getCaptchaUrl(){
					return baseUrl + "/user/getGraphicCaptcha?time=" + new Date().getTime()
				},
				
				getUploadAvatarUrl(){
					return baseUrl + "/user/uploadAvatar"
				},
				
				deleteAvatar(fileName){
					const url = "/user/deleteAvatar?fileName="+fileName
					usuallyGet(url)
				},
				
				changeSelfInfo(dto,succ){
					const url = "/user/changeSelfInfo"
					usuallyPost(url,dto,succ)
				},
				
				hasCollected(postId,succ){
					const url = "/user/hasCollected?postId="+postId
					usuallyGet(url,succ)
				},
				addCollect(postId,succ){
					const url = "/user/addCollect?postId="+postId
					usuallyPost(url,{},succ)
				},
				removeCollect(postId,succ){
					const url = "/user/removeCollect?postId="+postId
					usuallyPost(url,{},succ)
				},
			},
			post:{//注意这个post是功能划分，不是http get和post的意思
				getUploadPictureUrl(){
					return baseUrl + "/post/uploadPicture"
				},
				deletePicture(fileName){
					const url = "/post/deletePicture?fileName="+fileName
					usuallyGet(url)
				},
				getAllSelfSeries(succ){
					const url = "/post/getAllSelfSeries"
					usuallyGet(url,succ)
				},
				getById(id,key,succ,fail){
					if(!key)key=""
					const url = "/post/getById?id="+id+"&key="+key
					usuallyGet(url,succ,fail)
				},
				add(dto,succ){
					const url = "/post/add"
					usuallyPost(url,dto,succ)
				},
				update(dto,succ){
					const url = "/post/update"
					usuallyPost(url,dto,succ)
				},
				delete(id,succ){
					const url = "/post/delete?id="+id
					usuallyPost(url,{},succ)
				},
				findCurrentUserPosts(currentPage,pageSize,succ){
					const url = "/post/findCurrentUserPosts?currentPage="+currentPage+"&pageSize="+pageSize
					usuallyGet(url,succ)
				},
				hasVoted(id,succ){
					const url = "/post/hasVoted?id="+id
					usuallyGet(url,succ)
				},
				vote(id,optionId,succ){
					const url = "/post/vote?id="+id+"&optionId="+optionId
					usuallyPost(url,{},succ)
				},
				tryByNumber(number,key,succ,fail){
					const url = "/post/tryByNumber?number="+number+"&key="+key
					usuallyGet(url,succ,fail)
				},
				findPosts(currentPage,pageSize,keyword,category,timeOrHot,succ){
					const url = "/post/findPosts?currentPage="+currentPage+"&pageSize="+pageSize+"&keyword="+keyword+"&category="+category+"&timeOrHot="+timeOrHot
					usuallyGet(url,succ)
				},
				findPostsInCollects(currentPage,pageSize,succ){
					const url = "/post/findPostsInCollects?currentPage="+currentPage+"&pageSize="+pageSize
					usuallyGet(url,succ)
				},
				findAllPostsInSameSeries(userId,series,succ){
					const url = "/post/findAllPostsInSameSeries?userId="+userId+"&series="+series
					usuallyGet(url,succ)
				},
				getUserInfosOfVotesInPost(id,succ){
					const url = "/post/getUserInfosOfVotesInPost?id="+id
					usuallyGet(url,succ)
				},
				getUsernamesOfPost(id){
					const url = "/post/getUsernamesOfPost?id="+id
					usuallyGet(url,succ)
				},
				canCurrentUserAddPost(succ){
					const url = "/post/canCurrentUserAddPost"
					usuallyGet(url,succ)
				}
			},
			comment:{
				findPostComments(currentPage,pageSize,postId,timeOrHot,succ){
					const url = "/comment/findPostComments?currentPage="+currentPage+"&pageSize="+pageSize+"&postId="+postId+"&timeOrHot="+timeOrHot
					usuallyGet(url,succ)
				},
				findPresentUserComments(currentPage,pageSize,succ){
					const url = "/comment/findPresentUserComments?currentPage="+currentPage+"&pageSize="+pageSize
					usuallyGet(url,succ)
				},
				add(dto,succ){
					const url = "/comment/add"
					usuallyPost(url,dto,succ)
				},
				reply(dto,succ){
					const url = "/comment/reply"
					usuallyPost(url,dto,succ)
				},
				delete(id,succ){
					const url = "/comment/delete?id="+id
					usuallyPost(url,{},succ)
				},
				like(id,succ){
					const url = "/comment/like?id="+id
					usuallyPost(url,{},succ)
				},
				cancelLike(id,succ){
					const url = "/comment/cancelLike?id="+id
					usuallyPost(url,{},succ)
				},
			}
		}
	}
}
