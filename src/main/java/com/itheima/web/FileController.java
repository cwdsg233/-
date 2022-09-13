package com.itheima.web;

import com.itheima.common.R;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RequestMapping("/common")
@RestController
public class FileController {

    @Value("${fileDir}")
    private String fileDir;

    /**
     * 文件上传:: 把文件保存到指定的位置去！
     * @return 上传成功之后，要返回文件的名字。
     */
    @PostMapping("/upload")
    public R upload(MultipartFile file) throws IOException {

        //1. 文件夹一定存在吗？ 2. 文件名固定是 aa 不合适！名字必须是唯一的。 3. 文件的后缀一定是jpg?
        //file.transferTo(new File("D:/heima132/aa.jpg"));

        //1. 构建存储的文件位置
        File dir = new File(fileDir);
        if(!dir.exists()){
            dir.mkdirs();
        }

        //2. 处理文件的名字
        String newName = UUID.randomUUID().toString().replaceAll("-", "");

        //3. 处理文件的后缀

        //3.1 获取原始的名字  abc.png
        String originalFilename = file.getOriginalFilename();
        //originalFilename.split("\\.")[1]
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //新的文件名
        String fileName = newName + suffix;

        //4. 保存文件
        file.transferTo(new File(dir ,fileName ));

        return R.success(fileName);
    }


    /**
     * 文件下载
     * @param name
     * @param resp
     */
    @GetMapping("/download")
    public void download(String name , HttpServletResponse resp) throws IOException {

        //1. 读取文件的输入流
        FileInputStream fis = new FileInputStream(new File(fileDir , name));

        //2. 得到输出流:: 给客户端， 从resp得到
        OutputStream os = resp.getOutputStream();

        //3. 让IO对接
        IOUtils.copy(fis , os);

        fis.close();

        /*byte [] buffer = new byte[1024];
        int len = 0 ;
        while( (len = fis.read(buffer)) != -1){
            os.write(buffer ,  0 , len);
        }*/

    }
}
