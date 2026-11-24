package com.yutong.modules.user.convert;

import cn.hutool.core.util.StrUtil;
import com.yutong.modules.user.dto.SafetyDictItemDTO;
import com.yutong.modules.user.dto.SafetyDictSupperDTO;
import com.yutong.modules.user.entity.SafetyFieldManage;
import com.yutong.modules.user.vo.SafetyDictItemVO;

public class DictConvert {

    public static SafetyFieldManage getField(SafetyDictSupperDTO dto){
        SafetyFieldManage safetyFieldManage = new SafetyFieldManage();
        safetyFieldManage.setId(dto.getId());
        safetyFieldManage.setFieldName(dto.getSupperName());
        safetyFieldManage.setFieldCode(dto.getSupperCode());
        safetyFieldManage.setFieldType(dto.getFieldType());
        safetyFieldManage.setFieldSort(dto.getSupperOrder());
        return  safetyFieldManage;
    }

    public static SafetyFieldManage getField(SafetyDictItemDTO dto){
        SafetyFieldManage safetyFieldManage = new SafetyFieldManage();
        safetyFieldManage.setId(dto.getId());
        safetyFieldManage.setFieldName(dto.getItemName());
        safetyFieldManage.setFieldCode(dto.getItemCode());
        safetyFieldManage.setFieldSort(dto.getItemOrder());
        if(dto.getPid() != null){
            safetyFieldManage.setParentId(dto.getPid());
        }else{
            safetyFieldManage.setParentId(dto.getSupperId());
        }
        if(StrUtil.isNotBlank(dto.getFieldType())){
            safetyFieldManage.setFieldType(dto.getFieldType());
        }else{
            safetyFieldManage.setFieldType(dto.getSupperCode());
        }
        return  safetyFieldManage;
    }

    public static SafetyDictItemDTO getDictItemDTO(SafetyFieldManage safetyFieldManage){
        SafetyDictItemDTO safetyDictItemDTO = new SafetyDictItemDTO();
        safetyDictItemDTO.setId(safetyFieldManage.getId());
        safetyDictItemDTO.setItemName(safetyFieldManage.getFieldName());
        safetyDictItemDTO.setItemCode(safetyFieldManage.getFieldCode());
        safetyDictItemDTO.setPid(safetyFieldManage.getParentId());
        safetyDictItemDTO.setItemOrder(safetyFieldManage.getFieldSort());
        return  safetyDictItemDTO;
    }

    public static SafetyDictSupperDTO getDictSupperDTO (SafetyFieldManage safetyFieldManage){
        SafetyDictSupperDTO supperDTO = new SafetyDictSupperDTO();
        supperDTO.setId(safetyFieldManage.getId());
        supperDTO.setSupperName(safetyFieldManage.getFieldName());
        supperDTO.setSupperCode(safetyFieldManage.getFieldCode());
        supperDTO.setSupperOrder(safetyFieldManage.getFieldSort());
        return  supperDTO;
    }

    public static SafetyDictItemVO getDictItemVO(SafetyFieldManage safetyFieldManage){
        SafetyDictItemVO safetyDictItemVO = new SafetyDictItemVO();
        safetyDictItemVO.setId(safetyFieldManage.getId());
        safetyDictItemVO.setItemName(safetyFieldManage.getFieldName());
        safetyDictItemVO.setItemCode(safetyFieldManage.getFieldCode());
        safetyDictItemVO.setPid(safetyFieldManage.getParentId());
        safetyDictItemVO.setItemOrder(safetyFieldManage.getFieldSort());
        return  safetyDictItemVO;
    }
}
