package personal.lyh.voter.other;

import cn.hutool.core.map.MapUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import personal.lyh.voter.entity.Result;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@Component
public class UploadUtil
{
    @Value("${server.port}")
    private int port;

    //暂时统一将上传的文件放在同一个文件夹files里
    public Result uploadFile(MultipartFile file, String putDir) throws UnknownHostException {
        //上传的文件大小限制在application.properties中配置spring.servlet.multipart.max...

        //获取文件后缀名
        String fileName = FilenameUtils.getBaseName(file.getOriginalFilename());
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        //文件名使用UUID和时间组成的，来保证文件名不重复
//        String suffix = UUID.randomUUID().toString().replace("-", "") +
//                new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss").format(new Date());
        long suffix = new Date().getTime();
        String newFileName = fileName + "-" + suffix + "." + extension;

        //应该把文件存放到的路径位置
        String toPath = MyConstant.resourceBasePath + "/" + putDir;

        //files文件夹不存在的话则创建文件夹
        File folder = new File(toPath);
        if (!folder.exists())
            folder.mkdirs();

        try {
            file.transferTo(new File(toPath, newFileName));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("文件上传失败");
        }

        //得到文件的逻辑url，在WebMvcConfig中有配置逻辑路径与物理路径的映射
        InetAddress localHost = InetAddress.getLocalHost();
        String logicPath = "http://" + localHost.getHostAddress() + ":" + port + "/static/" + putDir + "/" + newFileName;

        return Result.success("", MapUtil.builder().put("url", logicPath).map());
    }

    //根据文件名和放置文件夹，删除目标文件
    public Result deleteFile(String fileName, String putDir){
        if(new java.io.File(MyConstant.resourceBasePath + "/" + putDir + "/" +fileName).delete())
            return Result.success();
        return Result.fail();
    }

}
