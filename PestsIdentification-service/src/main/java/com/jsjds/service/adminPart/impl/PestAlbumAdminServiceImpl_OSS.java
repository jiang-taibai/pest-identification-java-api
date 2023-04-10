package com.jsjds.service.adminPart.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsjds.mapper.InsectImagesMapper;
import com.jsjds.pojo.InsectImages;
import com.jsjds.pojo.vo.PestImageIdAndOSSUrl;
import com.jsjds.service.JsonService;
import com.jsjds.service.adminPart.PestAlbumAdminService;
import com.jsjds.utils.ImageUtil;
import com.jsjds.utils.ResponseWrapper;
import com.jsjds.utils.UploadQiNiuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;

/**
 * <p>Creation Time: 2022-03-11 10:20:00</p>
 * <p>Description: TODO</p>
 *
 * @author 太白
 */
@Service("PestAlbumAdminServiceImpl_OSS")
public class PestAlbumAdminServiceImpl_OSS implements PestAlbumAdminService {

    @Autowired
    private InsectImagesMapper insectImagesMapper;

    @Autowired
    private JsonService jsonService;

    @Resource
    private UploadQiNiuUtil uploadQiNiuUtil;

    @Value("${pest-image-path.defaultAlbumName}")
    public String defaultAlbumName;

    @Value("${pest-image-path.oss-domain}")
    public String ossDomain;


    /**
     * 根据物种ID获得一个分类相册的Map，Map以分类为名称，以图片名为链表value
     *
     * @param speciesId 物种ID
     * @return Map<String, List < PestImageIdAndOSSUrl>> Map以分类为名称，以图片名为链表value
     */
    @Override
    public ResponseWrapper getAlbumImagesRelativePath(String speciesId) {
        InsectImages record = new InsectImages();
        record.setSpeciesId(speciesId);
        List<InsectImages> insectImages = insectImagesMapper.select(record);
        Map<String, List<PestImageIdAndOSSUrl>> res = new HashMap<>();
        for (InsectImages insectImage : insectImages) {
            if (!res.containsKey(insectImage.getImgType())) {
                res.put(insectImage.getImgType(), new ArrayList<>());
            }
            res.get(insectImage.getImgType()).add(
                    new PestImageIdAndOSSUrl(insectImage.getImgPath(),
                            ossDomain + insectImage.getImgPath()));
        }
        return ResponseWrapper.markSuccess(res);
    }

    /**
     * 根据speciesId和传入的Map<String, List<String>>的Json数据，此Json数据格式与{@link PestAlbumAdminService}类的getAlbumImagesRelativePath方法返回的原理相同
     *
     * @param stateJson 状态Json
     * @param speciesId 物种ID
     * @return 是否修改成功
     */
    @Override
    public ResponseWrapper changeAlbumImage(String stateJson, String speciesId) {
        // 首先获取所有的图片信息
        HashMap<String, List<PestImageIdAndOSSUrl>> allImageInfo = (HashMap<String, List<PestImageIdAndOSSUrl>>) getAlbumImagesRelativePath(speciesId).getData();
        // 标记所有图片为死亡
        HashMap<String, Boolean> isAlive = new HashMap<>();
        for (Map.Entry<String, List<PestImageIdAndOSSUrl>> entry : allImageInfo.entrySet()) {
            entry.getValue().forEach(p -> isAlive.put(p.getImgId(), false));
        }
        // 再修改所有图片信息
        InsectImages example = new InsectImages();
        example.setSpeciesId(speciesId);
        Map<String, List<String>> map = toMap(stateJson);
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String key = entry.getKey().intern();
            String imgType = entry.getKey();
            for (String imgPath : entry.getValue()) {
                example.setImgPath(imgPath);
                InsectImages target = insectImagesMapper.selectOne(example);
                if (target != null) {
                    // 标记为存活
                    isAlive.put(imgPath, true);
                    // 如果分类不一致还得更新
                    if (!target.getImgType().equals(imgType)) {
                        insectImagesMapper.changeImageType(speciesId, imgPath, imgType);
                    }
                }
            }
        }
        // 将未存活的图片删除
        for (Map.Entry<String, Boolean> entry : isAlive.entrySet()) {
            if (entry.getValue().equals(false)) {
                // TODO 测试中不删除七牛云文件
                // Boolean isDeleted = UploadQiNiuUtil.deleteFile(entry.getKey());
                boolean isDeleted = true;
                if (isDeleted) {
                    insectImagesMapper.deleteImageByImgPath(entry.getKey());
                }
            }
        }
        return ResponseWrapper.markSuccessButNoData();
    }

    /**
     * 管理员上传图片
     *
     * @param speciesId     害虫ID，上传给那个害虫
     * @param imgBase64Data 图片的Base64码
     * @return 是否上传成功以及相对路径
     */
    @Override
    public ResponseWrapper uploadImage(String speciesId, String imgBase64Data) {
        InputStream inputStream = ImageUtil.base64ToInputStream(imgBase64Data);
        String key = uploadQiNiuUtil.upload(inputStream, 2);
        if (key != null) {
            InsectImages temp = new InsectImages();
            temp.setSpeciesId(speciesId);
            temp.setImgType(defaultAlbumName);
            temp.setImgPath(key);
            insectImagesMapper.insert(temp);
            return ResponseWrapper.markSuccess(key);
        }
        return ResponseWrapper.markError("上传失败");
    }

    private Map<String, List<String>> toMap(String json) {
        //解决无法解析/的问题
        // jsonStr = jsonStr.replace("\\", "\\\\");
        Map<String, JSONArray> mapObj = JSONObject.parseObject(json, Map.class);
        Map<String, List<String>> maps = new HashMap<>();
        for (Map.Entry<String, JSONArray> item : mapObj.entrySet()) {
            String key = item.getKey();
            JSONArray jsonArray = item.getValue();

            List<String> list = JSONObject.parseArray(jsonArray.toJSONString(), String.class);
            // for (String str : list) {
            //     //换回来
            //     str = str.replace("\\\\", "\\");
            //     System.out.println(str);
            // }
            maps.put(key, list);
        }
        return maps;
    }
}
