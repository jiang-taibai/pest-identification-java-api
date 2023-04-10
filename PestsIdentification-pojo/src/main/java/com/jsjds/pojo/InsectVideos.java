package com.jsjds.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "insect_videos")
public class InsectVideos implements Serializable {
    /**
     * 害虫种编码
     */
    @Id
    @Column(name = "species_id")
    private String speciesId;

    /**
     * 视频的路径
     */
    @Id
    @Column(name = "video_path")
    private String videoPath;

    private static final long serialVersionUID = 1L;

    public InsectVideos(String speciesId, String videoPath) {
        this.speciesId = speciesId;
        this.videoPath = videoPath;
    }

    public InsectVideos() {
        super();
    }

    /**
     * 获取害虫种编码
     *
     * @return species_id - 害虫种编码
     */
    public String getSpeciesId() {
        return speciesId;
    }

    /**
     * 设置害虫种编码
     *
     * @param speciesId 害虫种编码
     */
    public void setSpeciesId(String speciesId) {
        this.speciesId = speciesId;
    }

    /**
     * 获取视频的路径
     *
     * @return video_path - 视频的路径
     */
    public String getVideoPath() {
        return videoPath;
    }

    /**
     * 设置视频的路径
     *
     * @param videoPath 视频的路径
     */
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", speciesId=").append(speciesId);
        sb.append(", videoPath=").append(videoPath);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}