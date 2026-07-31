package com.hik.adapter.activity;

import com.hik.adapter.model.PersonInfo;
import com.hik.adapter.model.PersonResult;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * 海康校验 Activity 接口
 * Activity 是 Temporal 中实际执行业务逻辑的单元（调用外部API等）
 */
@ActivityInterface
public interface HikActivity {

    /**
     * 调用海康 API 校验单个人员
     *
     * @param person 人员信息
     * @return 校验结果
     */
    @ActivityMethod
    PersonResult verifySinglePerson(PersonInfo person);
}
