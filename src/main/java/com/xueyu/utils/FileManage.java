package com.xueyu.utils;


import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件管理工具
 *
 * @author durance
 */
public class FileManage {

    /**
     * 根据路径和文件名删除图片
     *
     * @param path 路径和文件名
     * @return 1表示删除成功
     * 0表示删除失败
     * -1表示文件不存在
     */
    public static int delFileByPath(String path){
        int resultInfo;
        File file = new File(path);
        //文件是否存在
        if (file.exists()) {
            //存在就删了，返回1
            if (file.delete()) {
                resultInfo = 1;
            } else {
                resultInfo = 0;
            }
        } else {
            resultInfo = -1;
        }
        return resultInfo;
    }

    /**
     * 上传保存图片
     *
     * @param file       文件
     * @param uploadPath 上传路径
     * @return 文件名
     * @throws IOException
     */
    public static String saveUploadFile(MultipartFile file, String uploadPath) throws IOException {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        String subfix = fileName.substring(fileName.lastIndexOf("."));
        String uploadFileName = UUID.randomUUID().toString() + subfix;
        System.out.println("上传文件名 : " + uploadFileName);
        //如果路径不存在，创建一个
        File realPath = new File(uploadPath);
        if (!realPath.exists()) {
            realPath.mkdir();
        }
        System.out.println("上传文件保存地址："+realPath);
        file.transferTo(new File(uploadPath+"/"+uploadFileName));
        return uploadFileName;
    }

}
