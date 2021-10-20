package com.sonic.simple.services.impl;

import com.sonic.simple.dao.ModulesRepository;
import com.sonic.simple.models.Modules;
import com.sonic.simple.services.ModulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModulesServiceImpl implements ModulesService {
    @Autowired
    private ModulesRepository modulesRepository;

    @Override
    public void save(Modules modules) {
        modulesRepository.save(modules);
    }

    @Override
    public boolean delete(int id) {
        if (modulesRepository.existsById(id)) {
            modulesRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Modules> findByProjectId(int projectId) {
        return modulesRepository.findByProjectId(projectId);
    }

    @Override
    public Modules findById(int id) {
        if (modulesRepository.existsById(id)) {
            return modulesRepository.findById(id).get();
        } else {
            return null;
        }
    }
}
