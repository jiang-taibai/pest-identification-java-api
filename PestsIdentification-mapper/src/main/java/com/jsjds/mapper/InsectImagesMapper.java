package com.jsjds.mapper;

import com.jsjds.my.mapper.MyMapper;
import com.jsjds.pojo.InsectImages;
import org.springframework.stereotype.Component;

@Component
public interface InsectImagesMapper extends MyMapper<InsectImages> {

    /**
     * 更新害虫ID
     *
     * @param oldSpeciesId 旧ID
     * @param newSpeciesId 新ID
     */
    public void updateSpeciesId(String oldSpeciesId, String newSpeciesId);

    public void changeImageType(String speciesId, String imgPath, String newImgType);

    /**
     * 将文件名为imgPath的记录删除
     *
     * @param imgPath 文件名
     */
    public void deleteImageByImgPath(String imgPath);

}