package com.jsjds.pojo.vo;

import lombok.Data;

/**
 * <p>Creation Time: 2021-12-24 13:51:41</p>
 * <p>Description: TODO</p>
 *
 * @author 太白
 */
@Data
public class PestImageIdAndOSSUrl {

    public String imgId;

    public String ossUrl;

    public PestImageIdAndOSSUrl() {
    }

    public PestImageIdAndOSSUrl(String imgId, String ossUrl) {
        this.imgId = imgId;
        this.ossUrl = ossUrl;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getOssUrl() {
        return ossUrl;
    }

    public void setOssUrl(String ossUrl) {
        this.ossUrl = ossUrl;
    }

    @Override
    public String toString() {
        return "PestImageIdAndOSSUrl{" +
                "imgId='" + imgId + '\'' +
                ", ossUrl='" + ossUrl + '\'' +
                '}';
    }
}
