# uView_default
uni-app初始化的版本库，使用的是uView的ui库进行了简单的封装

# uView 项目地址：
https://v1.uviewui.com/

## 封装HTTP请求
一般建议对此进行token的配置，说明：由于本拦截是每次请求都会调用的，如果在此写入token到header中，即使再次您重新登录，token发生变化，也无需再次调用本拦截
## 响应拦截
响应拦截器中默认返回的是response.data，如果您的需求比较特殊，需要返回response，请在"$u.http.setConfig"配置 originalData为true，如果配置了，服务端返回的"response.statusCode"不为"200"的时候，将不会自动弹出"modal"框，请自行 在响应拦截器中配置相关行为。
## 请求拦截
## api集中管理
