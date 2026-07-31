package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchResult implements Serializable {
    private String batchId;
    private int total;
    private int successCount;
    private int failCount;
    private List<PersonResult> details;
    private String status;
}
