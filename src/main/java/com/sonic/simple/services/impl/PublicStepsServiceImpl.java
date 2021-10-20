package com.sonic.simple.services.impl;

import com.sonic.simple.dao.PublicStepsRepository;
import com.sonic.simple.models.PublicSteps;
import com.sonic.simple.services.PublicStepsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author ZhouYiXun
 * @des 公共步骤逻辑实现
 * @date 2021/8/20 17:51
 */
@Service
public class PublicStepsServiceImpl implements PublicStepsService {
    @Autowired
    private PublicStepsRepository publicStepsRepository;

    @Override
    public Page<PublicSteps> findByProjectId(int projectId, Pageable pageable) {
        return publicStepsRepository.findByProjectId(projectId, pageable);
    }

    @Override
    public List<Map<Integer, String>> findByProjectIdAndPlatform(int projectId, int platform) {
        return publicStepsRepository.findByProjectIdAndPlatform(projectId, platform);
    }

    @Override
    public PublicSteps save(PublicSteps publicSteps) {
        publicStepsRepository.save(publicSteps);
        return publicSteps;
    }

    @Override
    public boolean delete(int id) {
        if (publicStepsRepository.existsById(id)) {
            publicStepsRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public PublicSteps findById(int id) {
        if (publicStepsRepository.existsById(id)) {
            return publicStepsRepository.findById(id).get();
        } else {
            return null;
        }
    }
}
