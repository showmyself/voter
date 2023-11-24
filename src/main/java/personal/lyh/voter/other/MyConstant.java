package personal.lyh.voter.other;

public class MyConstant
{

    public static String CacheType_EmailCode="CacheType_EmailCode";
    public static String CacheType_IpUrlLimit ="CacheType_UrlLimit";
    public static String CacheType_User="CacheType_User";
    public static String CacheType_Post="CacheType_Post";
    //缓存弄这么多暂时够了，又不是商用，有个演示即可，如果缓存列表会导致一些复杂暂时不弄

    public static String passwordSalt="voter1";

    public static Integer registerAddMark=0;//注册时初始化积分，暂时设置为零，如果为50，用户每注册一个账号就可以新建，会造成问题
    public static Integer voteAddMark=10;//给他人投票增加积分
    public static Integer newPostCostMark=0;//新增投票帖消耗的积分。因测试暂时为0

    //默认头像名称
    public static String defaultIconName="default.png";

    public static String resourceBasePath=System.getProperty("user.dir") + "/src/main/resources/static";

}
