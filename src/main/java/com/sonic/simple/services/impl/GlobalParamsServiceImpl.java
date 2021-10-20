package com.sonic.simple.services.impl;

import com.sonic.simple.dao.GlobalParamsRepository;
import com.sonic.simple.models.GlobalParams;
import com.sonic.simple.services.GlobalParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZhouYiXun
 * @des
 * @date 2021/10/9 23:28
 */
@Service
public class GlobalParamsServiceImpl implements GlobalParamsService {
    @Autowired
    private GlobalParamsRepository globalParamsRepository;

    @Override
    public List<GlobalParams> findAll(int projectId) {
        return globalParamsRepository.findByProjectId(projectId);
    }

    @Override
    public boolean delete(int id) {
        if (globalParamsRepository.existsById(id)) {
            globalParamsRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void save(GlobalParams globalParams) {
        globalParamsRepository.save(globalParams);
    }

    @Override
    public GlobalParams findById(int id) {
        if (globalParamsRepository.existsById(id)) {
            return globalParamsRepository.findById(id).get();
        } else {
            return null;
        }
    }
}
