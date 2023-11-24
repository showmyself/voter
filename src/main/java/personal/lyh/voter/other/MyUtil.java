package personal.lyh.voter.other;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import personal.lyh.voter.extend.ServiceException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MyUtil
{
    //注入静态变量的方式：给类注解@Component，然后在其内用方法来注入
    private static String port;
    @Value("${server.port}")
    public void setPort(String port) {
        MyUtil.port = port;
    }

    //获取本运行程序的ip:port
    public static String getIpAndPort(){
        try{
            String ip = InetAddress.getLocalHost().getHostAddress();
            return ip+":"+port;
        }catch (Exception e){
            return "unknown";
        }
    }

    //对密码加密
    public static String encrypt(String origin, String salt)
    {
        return origin;
        //return SecureUtil.md5(origin + salt);
    }

    //根据路径获取末尾字符串
    public static String getLastStrFromPath(String path){
        return getLastStrFromPath(path,"/");
    }
    public static String getLastStrFromPath(String path,String splitChar){
        return path.substring(path.lastIndexOf(splitChar)+1);
    }

    //获取当前线程的用户id
    public static Long getCurrentUserId(){
        return getCurrentUserId(true);
    }
    public static Long getCurrentUserId(boolean throwExceptionIfNull){

        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof String){
            try{
                return Long.parseLong((String)principal);
            }catch (Exception ignored){ }
        }

        if(throwExceptionIfNull)
            throw new ServiceException("尚未登录");

        return null;

    }

    //验证邮箱格式
    public static boolean isEmail(String string) {
        if (string == null)
            return false;
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches())
            return true;
        else
            return false;
    }

    //验证码
    public static void returnGraphicCaptcha(HttpServletResponse response, HttpServletRequest request, HttpSession session)
    {
        //根据Hutool工具类获取验证码对象
        //定义图形验证码的长和宽,验证码的位数，干扰线的条数
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(116, 36,4,30);

        //将验证码放入session
        session.setAttribute("captcha",lineCaptcha.getCode());
        session.setMaxInactiveInterval(10*60);//以秒为单位，即在没有活动10分钟后，session将失效

        try{
            ServletOutputStream outputStream = response.getOutputStream();
            lineCaptcha.write(response.getOutputStream());//将验证码图片写到response中
            outputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //生成1000到9999的随机数：
    public static String generateValidCode(){
        return String.valueOf(new Random().nextInt(9000)+1000);//此为1000到9999内的随机数
    }

    //获取当前日期的前第七天的日期，并且时刻从零开始
    public static Date getPreviousSeventhDate(){
        Date aimDate=new Date();
        for(int i=0;i<7;++i)
            aimDate=yesterday(aimDate);
        aimDate=getStartOfDay(aimDate);
        return aimDate;
    }

    //返回目标日期的昨天
    public static Date yesterday(Date targetDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(targetDate);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        return calendar.getTime();
    }

    //返回目标日期的明天
    public static Date tomorrow(Date targetDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(targetDate);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        return calendar.getTime();
    }

    // 获得某天最大时间，到达23:59:59
    public static Date getEndOfDay(Date date) {
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(date);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
        calendarEnd.set(Calendar.MINUTE, 59);
        calendarEnd.set(Calendar.SECOND, 59);
        //防止mysql自动加一秒,毫秒设为0
        calendarEnd.set(Calendar.MILLISECOND, 0);
        return calendarEnd.getTime();
    }

    // 获得某天最小时间，（应该是到00:00:00）
    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());

    }

    //检查大小是否在目标范围之内，参数一是根据...io.File.getSize()或getUsableSpace或getTotalSpace获得的
    public static boolean checkFileSize(Long len, int size, String unit)
    {
        double fileSize = 0;
        if ("B".equals(unit.toUpperCase()))
        {
            fileSize = (double) len;
        }
        else if ("K".equals(unit.toUpperCase()))
        {
            fileSize = (double) len / 1024;
            System.out.println("目标大小："+fileSize+" KB");
        }
        else if ("M".equals(unit.toUpperCase()))
        {
            fileSize = (double) len / 1048576;
            System.out.println("目标大小："+fileSize+" MB");
        }
        else if ("G".equals(unit.toUpperCase()))
        {
            fileSize = (double) len / 1073741824;
            System.out.println("目标大小："+fileSize+" GB");
        }
        if (fileSize > size)
            return false;
        return true;
    }

}
