package com.xupt.util;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


/**
 * @Auther: yhn
 * @Date: 2020/5/26 17:18
 */
public class QiNiuVideoUpload {


    //构造一个带指定 Region 对象的配置类
    private static Configuration cfg = new Configuration(Region.region2());//华南
    //...其他参数参考类注释

    public static void main(String[] args) {
        delete("ce971c49-d347-49ea-b98c-2e228cef4807",1);
    }

    /**
     * 删除文件
     *
     * @param key fileKey
     * @return String
     */
    public static String delete(String key, int i) {
        String secretKey = "fi4vHmdHQKM3hXvECRrrO0l9qHwV2j3TP8E8IAde";
        String accessKey = "hDBMjX7WvFs7XpXhQ_hIVgoTOkbYmee2QJVVwRCl";
        Auth auth = Auth.create(accessKey, secretKey);
        String bucket;
        if (i == 1) {
            bucket = "pictureforlemon";
        } else {
            bucket = "videoforlemon";
        }
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
            return "删除成功";
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
        return "删除失败";
    }

    /**
     * 上传文件类
     *
     * @param fileName    文件名
     * @param inputStream 文件流
     * @param url         上传空间
     * @return String
     */
    private static String common(String fileName, InputStream inputStream, String url, String bucket) {
        //...生成上传凭证，然后准备上传
        UploadManager uploadManager = new UploadManager(cfg);

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String finalUrl = String.format("%s%s", url, fileName);

        String secretKey = "fi4vHmdHQKM3hXvECRrrO0l9qHwV2j3TP8E8IAde";
        String accessKey = "hDBMjX7WvFs7XpXhQ_hIVgoTOkbYmee2QJVVwRCl";
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);

        try {
            //文件流上传
            Response response = uploadManager.put(inputStream, fileName, upToken, null, null);
            if (!response.isOK()) {
                return null;
            }
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
        }
        return finalUrl;
    }

    /**
     * 上传封面
     *
     * @param fileName    文件名
     * @param inputStream 文件流
     * @return String
     */
    public static String uploadImage(String fileName, InputStream inputStream) {
        String imageUrl = "http://qsm9hhtja.hn-bkt.clouddn.com/";
        String bucketImage = "dushop";
        return common(fileName, inputStream, imageUrl, bucketImage);
    }


    /**
     * 上传视频
     *
     * @param fileName    文件名
     * @param inputStream 文件流
     * @return String
     */
    private static String uploadVideo(String fileName, InputStream inputStream) {
        String videoUrl = "http://qaxfk6qdw.bkt.clouddn.com/";
        String bucketVideo = "videoforlemon";
        return common(fileName, inputStream, videoUrl, bucketVideo);
    }

    /**
     * 文件处理
     *
     * @param file 文件名
     * @param type 文件类型  1：图片   0：视频
     * @return String
     * @throws IOException 异常
     */
    public static String dealWithVideo(MultipartFile file, int type) throws IOException {
        String finalUrl;
        InputStream inputStream = file.getInputStream();
        if (type == 0) {
            finalUrl = uploadVideo(UUID.randomUUID().toString(), inputStream);
        } else {
            finalUrl = uploadImage(UUID.randomUUID().toString(), inputStream);
        }
        return finalUrl;
    }
}
