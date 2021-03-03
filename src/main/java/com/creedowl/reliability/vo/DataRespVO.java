package com.creedowl.reliability.vo;

import com.creedowl.reliability.entity.SourceData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DataRespVO {
    private String uuid;
    private List<SourceData> data;
}
