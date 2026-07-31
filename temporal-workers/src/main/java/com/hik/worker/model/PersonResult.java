package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonResult implements Serializable {
    private String personId;
    private boolean passed;
    private String errorMsg;
    private String rawResponse;

    public static PersonResult success(String personId, String rawResponse) {
        return PersonResult.builder().personId(personId).passed(true).rawResponse(rawResponse).build();
    }

    public static PersonResult fail(String personId, String errorMsg) {
        return PersonResult.builder().personId(personId).passed(false).errorMsg(errorMsg).build();
    }
}
