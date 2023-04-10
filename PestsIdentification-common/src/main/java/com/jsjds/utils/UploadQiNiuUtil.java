package com.jsjds.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

@Component
public class UploadQiNiuUtil {

    /**
     * AK/SK 存储空间名称
     * 设置好账号的ACCESS_KEY和SECRET_KEY
     */
    @Value("${qiniu.access_key}")
    private String ACCESS_KEY;
    @Value("${qiniu.secret_key}")
    private String SECRET_KEY;

    /**
     * 要上传的空间
     * 七牛云空间存储空间名称
     */
    public final String bucket = "pest-identification";

    /**
     * 七牛绑定的自定义域名
     */
    public final String BUCKET_HOST_NAME = "http://resource.taibai.cloud";

    /**
     * 你的文件上传路径
     */
    public final String DOMAIN = "";

    /**
     * 获得token
     *
     * @param bucketName 存储空间名称
     * @return token
     */
    public String token(String bucketName) {
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String upToken = auth.uploadToken(bucketName);
        return upToken;
    }

    /**
     * 默认存储空间
     *
     * @return token
     */
    public String token() {
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String upToken = auth.uploadToken(bucket);
        return upToken;
    }

    /**
     * 上传图片
     *
     * @param file       图片
     * @param fileName   文件名字
     * @param bucketName 存储空间名称
     * @param zoneName   获取存储区域名称
     * @return 文件名
     */
    public String uploadFile(File file, String fileName, String bucketName, Integer zoneName) {
        try {
            Zone zone = getZone(zoneName);
            Configuration cfg = new Configuration(zone);
            UploadManager uploadManager = new UploadManager(cfg);
            Response response = uploadManager.put(file, fileName, token(bucketName));

            // 解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
            return putRet.key;
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.out.println(fileName + "----文件上传失败----" + r.toString());
            try {
                System.err.println(r.bodyString());
                System.out.println(fileName + "----文件上传失败----" + r.toString());
            } catch (QiniuException ex2) {
                // ignore
            }
            return null;
        }

    }

    /**
     * 通过输入流上传
     *
     * @param inputStream 图片输入流
     * @param zoneName    存储区域
     * @return 上传后的文件名称
     */
    public String upload(InputStream inputStream, Integer zoneName) {
        Zone zone = getZone(zoneName);
        Configuration cfg = new Configuration(zone);
        //创建上传对象
        UploadManager uploadManager = new UploadManager(cfg);
        //返回
        String key = UUID.randomUUID().toString();
        try {
            try {
                Response response = uploadManager.put(inputStream, key, token(), null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                return putRet.key;
            } catch (QiniuException ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 获取存储区域
     *
     * @param zoneName 存储区域值
     * @return 存储区域
     */
    private Zone getZone(Integer zoneName) {
        Zone zone = Zone.zoneNa0();
        switch (zoneName) {
            case 0:
                return Zone.zone0();
            case 1:
                return Zone.zone1();
            case 2:
                return Zone.zone2();
        }
        return zone;

    }

    /**
     * 根据Key值删除云端文件
     *
     * @param key 文件名称
     * @return 是否删除成功
     */
    public Boolean deleteFile(String key) {
        Configuration cfg = new Configuration(Zone.zone2());
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            Response result = bucketManager.delete(bucket, key);
            if (result.statusCode == 200) {
                return true;
            }
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.out.println(key + "----删除失败----" + ex.code());
            System.out.println(key + "----删除失败----" + ex.response.toString());
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
        return false;
    }

}