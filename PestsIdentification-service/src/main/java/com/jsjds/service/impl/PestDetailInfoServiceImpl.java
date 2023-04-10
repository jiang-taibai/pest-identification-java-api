package com.jsjds.service.impl;

import com.jsjds.mapper.InsectVideosMapper;
import com.jsjds.mapper.PestSelectMapper;
import com.jsjds.pojo.InsectVideos;
import com.jsjds.pojo.vo.PestDetailInfoVO;
import com.jsjds.service.PestDetailInfoService;
import com.jsjds.utils.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>创建时间：2021/4/28 0:49</p>
 * <p>主要功能：TODO</p>
 *
 * @author 太白
 */
@Service
public class PestDetailInfoServiceImpl implements PestDetailInfoService {

    @Autowired
    public PestSelectMapper pestSelectMapper;
    @Resource
    public InsectVideosMapper insectVideosMapper;

    /**
     * 提供害虫ID，返回害虫的所有详细信息
     *
     * @param speciesId 害虫ID
     * @return 对应害虫的所有详细信息
     */
    @Override
    public ResponseWrapper getPestDetailInfo(String speciesId) {
        PestDetailInfoVO data = pestSelectMapper.queryPestsDetailInfoBySpeciesId(speciesId);
        return ResponseWrapper.markSuccess(data);
    }

    /**
     * 提供害虫ID，返回一个包含所有有关该害虫的视频链表
     *
     * @param speciesId 害虫ID
     * @return 一个包含所有有关该害虫的视频链表
     */
    @Override
    public ResponseWrapper getPestVideos(String speciesId) {
        InsectVideos record = new InsectVideos();
        record.setSpeciesId(speciesId);
        List<InsectVideos> insectVideos = insectVideosMapper.select(record);
        return ResponseWrapper.markSuccess(insectVideos);
    }
}
