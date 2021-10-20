package com.sonic.simple.services;

import com.sonic.simple.models.GlobalParams;

import java.util.List;

public interface GlobalParamsService {
    List<GlobalParams> findAll(int projectId);

    boolean delete(int id);

    void save(GlobalParams globalParams);

    GlobalParams findById(int id);
}
