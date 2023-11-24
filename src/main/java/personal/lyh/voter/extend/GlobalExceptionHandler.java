package personal.lyh.voter.extend;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import personal.lyh.voter.entity.Result;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 全局异常拦截
    @ExceptionHandler
    public Result handlerException(Exception e) {
        System.out.println("全局异常捕获---");
        e.printStackTrace();
        return Result.fail(e.getLocalizedMessage());
    }


    //捕获运行时异常
    @ExceptionHandler({RuntimeException.class})
    public Result handlerException(RuntimeException e) {

        if(e instanceof ServiceException){
            ServiceException se=(ServiceException)e;
            return Result.fail(se.getMessage()).withCode(se.code);
        }

        System.out.println(e.getMessage());
        return Result.fail("服务端处理错误："+e.getMessage());
    }


    //捕获参数格式验证错误
    @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
    public Result methodArgumentNotValidExceptionHandler(Exception e) {
        return Result.fail("参数格式验证不通过，详细："+e.getLocalizedMessage());
    }
    @ExceptionHandler({ HttpMessageNotReadableException.class })
    public Result methodArgumentNotValidExceptionHandler(HttpMessageNotReadableException e) {
        return Result.fail("参数格式验证不通过，详细："+e.getLocalizedMessage());
    }
}