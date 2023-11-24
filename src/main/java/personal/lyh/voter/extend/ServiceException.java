package personal.lyh.voter.extend;

public class ServiceException extends RuntimeException {

    public String code;
    public Object data;

    public ServiceException(String msg){
        super(msg);
    }

    public ServiceException withCode(String code){
        this.code=code;
        return this;
    }

    public ServiceException withData(Object data){
        this.data=data;
        return this;
    }
}
