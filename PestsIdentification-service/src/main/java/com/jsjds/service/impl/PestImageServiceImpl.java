package com.jsjds.service.impl;

import com.alibaba.fastjson.JSON;
import com.jsjds.mapper.InsectImagesMapper;
import com.jsjds.pojo.InsectImages;
import com.jsjds.service.PestImageService;
import com.jsjds.utils.FileToByteUtil;
import com.jsjds.utils.FileUtil;
import com.jsjds.utils.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * <p>创建时间：2021/4/26 18:51</p>
 * <p>主要功能：提供害虫ID，返回单张图片、多张不分类图片或分类图片</p>
 * <p> 单张图片用于首页每日一图、查询结果页的图片预览、害虫详情页的单张图片 </p>
 * <p> 多张不分类图片暂且不做 </p>
 * <p> 分类图片用于害虫详情图片页的展示 </p>
 *
 * @author 太白
 */
@Service
public class PestImageServiceImpl implements PestImageService {

    @Autowired
    public InsectImagesMapper insectImagesMapper;

    @Value("${pest-image-path.prefix}")
    public String prefixPath;

    @Value("${pest-image-path.oss-domain}")
    public String ossDomain;

    @Value("${pest-image-path.defaultAlbumName}")
    public String defaultAlbumName;

    /**
     * 通过害虫ID得到 1 张图片的相对路径
     *
     * @param speciesId 害虫ID
     * @return 通过害虫ID得到 1 张图片的OSS-Link
     */
    @Override
    public ResponseWrapper getPestImageAbsolutePathBySpeciesId(String speciesId) {
        List<InsectImages> insectImages = getPestImagesBarringDefaultAlbum(speciesId);
        if (insectImages == null || insectImages.size() == 0) return ResponseWrapper.markError("尚无图片资源");

        int index = (int) (Math.random() * insectImages.size());
        String ossLink = insectImages.get(index).getImgPath();
        return ResponseWrapper.markSuccess(ossDomain + ossLink);
    }

    /**
     * 通过害虫ID得到 多种分类的多张图片OSS-Link，
     * 标题为insect_type
     *
     * @param speciesId 害虫ID
     * @return data: { insect_type1: [OSS-Link1, OSS-Link2, OSS-Link3, ...] }
     */
    @Override
    public ResponseWrapper getPestClassifiedImageAbsolutePathBySpeciesId(String speciesId) {
        List<InsectImages> insectImages = getPestImagesBarringDefaultAlbum(speciesId);
        if (insectImages == null || insectImages.size() == 0) return ResponseWrapper.markError("尚无图片资源");

        Map<String, List<String>> res = new TreeMap<>();
        for (InsectImages insectImage : insectImages) {
            if (!res.containsKey(insectImage.getImgType())) res.put(insectImage.getImgType(), new ArrayList<>());
            res.get(insectImage.getImgType()).add(ossDomain + insectImage.getImgPath());
        }
        return ResponseWrapper.markSuccess(res);
    }

    /**
     * 通过害虫ID找到轮询六张图片，如果没有六张则找到最多的几张
     *
     * @param speciesId 害虫ID
     * @return 六张图片的OSS-Link, data: [OSS-Link1, OSS-Link2, OSS-Link3, ...]
     */
    @Override
    public ResponseWrapper getPestIntroImagesBySpeciesId(String speciesId) {
        List<InsectImages> insectImages = getPestImagesBarringDefaultAlbum(speciesId);
        if (insectImages == null || insectImages.size() == 0) return ResponseWrapper.markError("尚无图片资源");

        List<String> res = new ArrayList<>();
        Collections.shuffle(insectImages);
        for (int i = 0; i < Math.min(6, insectImages.size()); ++i) {
            res.add(ossDomain + insectImages.get(i).getImgPath());
        }

        return ResponseWrapper.markSuccess(res);
    }

    private List<InsectImages> getPestImagesBarringDefaultAlbum(String speciesId) {
        Example example = new Example(InsectImages.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("speciesId", speciesId);
        // 剔除默认分类下的图片
        criteria.andNotEqualTo("imgType", defaultAlbumName);
        return insectImagesMapper.selectByExample(example);
    }

}
