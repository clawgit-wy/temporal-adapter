package com.hik.worker.activity;

import com.hik.worker.model.PersonInfo;
import com.hik.worker.model.PersonResult;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface HikActivity {
    @ActivityMethod
    PersonResult verifySinglePerson(PersonInfo person);
}
