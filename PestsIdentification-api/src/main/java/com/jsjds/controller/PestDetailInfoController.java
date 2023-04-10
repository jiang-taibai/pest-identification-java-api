package com.jsjds.controller;

import com.jsjds.pojo.vo.PestDetailInfoVO;
import com.jsjds.service.PestDetailInfoService;
import com.jsjds.utils.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * <p>创建时间：2021/4/28 16:30</p>
 * <p>主要功能：TODO</p>
 *
 * @author 太白
 */
@CrossOrigin
@RestController
@RequestMapping("/pestDetailInfo")
public class PestDetailInfoController {

    @Autowired
    public PestDetailInfoService pestDetailInfoService;

    /**
     * 提供害虫ID，返回害虫的所有详细信息
     *
     * @param speciesId 害虫ID
     * @return 害虫的所有详细信息
     */
    @RequestMapping("/searchBySpeciesId")
    public ResponseWrapper getPestsDetailInfoBySpeciesId(@RequestParam("speciesId") String speciesId) {
        return pestDetailInfoService.getPestDetailInfo(speciesId);
    }

    /**
     * 提供害虫ID，返回一个包含所有有关该害虫的视频链表
     *
     * @param speciesId 害虫ID
     * @return 一个包含所有有关该害虫的视频链表
     */
    @GetMapping("/pestVideos")
    public ResponseWrapper getPestVideos(String speciesId) {
        return pestDetailInfoService.getPestVideos(speciesId);
    }

}
