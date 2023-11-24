package personal.lyh.voter.entity;

import lombok.Data;

@Data
public class Result
{
    boolean flag;
    String msg;
    Object data;
    String code;

    //此处不设置含参的重载，避免个人误认为第一个参数是data
    public static Result success(String msg,Object data){
        Result result=new Result();
        result.setFlag(true);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
    public static Result success(){
        Result result=new Result();
        result.setFlag(true);
        result.setMsg(null);
        result.setData(null);
        return result;
    }

    public static Result fail(String msg){
        Result result=new Result();
        result.setFlag(false);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
    public static Result fail(){
        Result result=new Result();
        result.setFlag(false);
        result.setMsg(null);
        result.setData(null);
        return result;
    }

    public Result withCode(String code){
        this.code=code;
        return this;
    }
}
