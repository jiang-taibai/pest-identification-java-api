package com.jsjds.service.adminPart.impl;

import com.jsjds.mapper.InsectImagesMapper;
import com.jsjds.pojo.InsectImages;
import com.jsjds.pojo.vo.PestImageIdAndOSSUrl;
import com.jsjds.service.FileOperationService;
import com.jsjds.service.JsonService;
import com.jsjds.service.adminPart.PestAlbumAdminService;
import com.jsjds.utils.FileUtil;
import com.jsjds.utils.ImageUtil;
import com.jsjds.utils.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.util.UUID;

//import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>创建时间：2021-07-01 20:14</p>
 * <p>主要功能：TODO</p>
 *
 * @author 太白
 */
@Service("PestAlbumAdminServiceImpl")
public class PestAlbumAdminServiceImpl implements PestAlbumAdminService {

    @Autowired
    private InsectImagesMapper insectImagesMapper;

    @Value("${pest-image-path.prefix}")
    public String prefixPath;
    @Value("${pest-image-path.defaultAlbumName}")
    public String defaultAlbumName;
    @Value("${pest-image-path.oss-domain}")
    public String ossDomain;

    @Autowired
    private JsonService jsonService;
    @Autowired
    private FileOperationService fileOperationService;

    /**
     * 根据物种ID获得一个分类相册的Map，Map以分类名为key，以该分类下的文件的相对路径为链表value
     *
     * @param speciesId 物种ID
     * @return data: { insect_type1: [{imgId, ossUrl}, {imgId, ossUrl}, {imgId, ossUrl}, ...] }
     */
    @Override
    public ResponseWrapper getAlbumImagesRelativePath(String speciesId) {
        InsectImages record = new InsectImages();
        record.setSpeciesId(speciesId);
        List<InsectImages> insectImages = insectImagesMapper.select(record);
        Map<String, List<PestImageIdAndOSSUrl>> res = new TreeMap<>();
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
        Map<String, List<String>> map = jsonService.jsonChangeMap(stateJson);
        fileOperationService.updateFolderByStateMap(map, speciesId);
        return ResponseWrapper.markSuccessButNoData();
    }

    private File getSomePestImageTopDir(String speciesId) {
        InsectImages insectImage = insectImagesMapper.selectByPrimaryKey(speciesId);
        String topPath_relative = insectImage.getImgPath();         // 相对路径
        String topPath_absolute = prefixPath + File.separator + topPath_relative;    // 绝对路径
        File topFile = new File(topPath_absolute);
        return topFile;
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
        InsectImages insectImages = insectImagesMapper.selectByPrimaryKey(speciesId);
        String fileName = UUID.randomUUID().toString();
        File file = ImageUtil.saveImg(imgBase64Data,
                prefixPath + File.separator + insectImages.getImgPath() + File.separator + defaultAlbumName, fileName);
        if (!file.exists()) {
            return ResponseWrapper.markError("保存图片失败");
        }
        File BaseDir = new File(prefixPath);
        String fileRelativePath = FileUtil.getRelativeFileName(BaseDir, file);
        return ResponseWrapper.markSuccess(fileRelativePath);
    }


}
