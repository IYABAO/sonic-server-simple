package org.cloud.sonic.simple.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.cloud.sonic.simple.mapper.*;
import org.cloud.sonic.simple.models.base.CommentPage;
import org.cloud.sonic.simple.models.base.TypeConverter;
import org.cloud.sonic.simple.models.domain.PublicSteps;
import org.cloud.sonic.simple.models.domain.PublicStepsSteps;
import org.cloud.sonic.simple.models.domain.Steps;
import org.cloud.sonic.simple.models.domain.StepsElements;
import org.cloud.sonic.simple.models.dto.ElementsDTO;
import org.cloud.sonic.simple.models.dto.StepsDTO;
import org.cloud.sonic.simple.models.http.StepSort;
import org.cloud.sonic.simple.services.StepsService;
import org.cloud.sonic.simple.services.impl.base.SonicServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZhouYiXun
 * @des 测试步骤实现
 * @date 2021/8/20 17:51
 */
@Service
public class StepsServiceImpl extends SonicServiceImpl<StepsMapper, Steps> implements StepsService {

    @Autowired private StepsMapper stepsMapper;
    @Autowired private ElementsMapper elementsMapper;
    @Autowired private PublicStepsMapper publicStepsMapper;
    @Autowired private PublicStepsStepsMapper publicStepsStepsMapper;
    @Autowired private StepsElementsMapper stepsElementsMapper;

    @Override
    public List<StepsDTO> findByCaseIdOrderBySort(int caseId) {

        List<StepsDTO> stepsDTOList = lambdaQuery().eq(Steps::getCaseId, caseId)
                .orderByAsc(Steps::getSort)
                .list()
                // 转换成DTO
                .stream().map(TypeConverter::convertTo).collect(Collectors.toList());
        // 填充elements
        stepsDTOList.forEach(e -> e.setElements(elementsMapper.listElementsByStepsId(e.getId())));
        return stepsDTOList;
    }

    @Override
    public boolean resetCaseId(int id) {
        if (existsById(id)) {
            Steps steps = baseMapper.selectById(id);
            steps.setCaseId(0);
            save(steps);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(int id) {
        if (existsById(id)) {
            Steps steps = baseMapper.selectById(id);
            publicStepsStepsMapper.delete(new QueryWrapper<PublicStepsSteps>().eq("steps_id", steps.getId()));
            baseMapper.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveStep(StepsDTO stepsDTO) {
        if (stepsDTO.getStepType().equals("publicStep")) {
            PublicSteps publicSteps = publicStepsMapper.selectById(Integer.parseInt(stepsDTO.getText()));
            if (publicSteps != null) {
                stepsDTO.setContent(publicSteps.getName());
            } else {
                stepsDTO.setContent("未知");
            }
        }

        // 设置排序为最后
        if (!existsById(stepsDTO.getId())) {
            stepsDTO.setSort(stepsMapper.findMaxSort() + 1);
        }
        Steps steps = stepsDTO.convertTo();
        save(steps);

        // 删除旧关系
        stepsElementsMapper.delete(new LambdaQueryWrapper<StepsElements>().eq(StepsElements::getStepsId, steps.getId()));

        // 保存element映射关系
        List<ElementsDTO> elements = stepsDTO.getElements();
        for (ElementsDTO element : elements) {
            stepsElementsMapper.insert(new StepsElements().setElementsId(element.getId()).setStepsId(steps.getId()));
        }
    }

    @Transactional
    @Override
    public StepsDTO findById(int id) {

        StepsDTO stepsDTO = baseMapper.selectById(id).convertTo();
        stepsDTO.setElements(elementsMapper.listElementsByStepsId(stepsDTO.getId()));
        return stepsDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sortSteps(StepSort stepSort) {

        List<Steps> stepsList = lambdaQuery().eq(Steps::getCaseId, stepSort.getCaseId())
                // <=
                .le(Steps::getSort, stepSort.getStartId())
                // >=
                .ge(Steps::getSort, stepSort.getEndId())
                .list();

        if (stepSort.getDirection().equals("down")) {
            for (int i = 0; i < stepsList.size() - 1; i++) {
                int temp = stepsList.get(stepsList.size() - 1).getSort();
                stepsList.get(stepsList.size() - 1).setSort(stepsList.get(i).getSort());
                stepsList.get(i).setSort(temp);
            }
        } else {
            for (int i = 0; i < stepsList.size() - 1; i++) {
                int temp = stepsList.get(0).getSort();
                stepsList.get(0).setSort(stepsList.get(stepsList.size() - 1 - i).getSort());
                stepsList.get(stepsList.size() - 1 - i).setSort(temp);
            }
        }
        saveOrUpdateBatch(stepsList);
    }

    @Override
    public CommentPage<StepsDTO> findByProjectIdAndPlatform(int projectId, int platform, Page<Steps> pageable) {

        Page<Steps> page = lambdaQuery().eq(Steps::getProjectId, projectId)
                .eq(Steps::getPlatform, platform)
                .orderByDesc(Steps::getId)
                .page(pageable);

        List<StepsDTO> stepsDTOList = page.getRecords()
                .stream().map(TypeConverter::convertTo).collect(Collectors.toList());

        for (StepsDTO stepsDTO : stepsDTOList) {
            stepsDTO.setElements(elementsMapper.listElementsByStepsId(stepsDTO.getId()));
        }

        return CommentPage.convertFrom(page, stepsDTOList);
    }

    @Override
    public List<Steps> listStepsByElementsId(int elementsId) {
        return stepsMapper.listStepsByElementId(elementsId);
    }

    @Override
    public boolean deleteByProjectId(int projectId) {
        return baseMapper.delete(new LambdaQueryWrapper<Steps>().eq(Steps::getProjectId, projectId)) > 0;
    }

    @Override
    public List<StepsDTO> listByPublicStepsId(int publicStepsId) {
        return stepsMapper.listByPublicStepsId(publicStepsId)
                // 填充elements
                .stream().map(e -> e.convertTo().setElements(elementsMapper.listElementsByStepsId(e.getId())))
                .collect(Collectors.toList());
    }
}
