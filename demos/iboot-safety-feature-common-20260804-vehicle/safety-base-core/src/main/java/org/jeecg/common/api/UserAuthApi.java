package org.jeecg.common.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(value = "iboot-safety-system")
public interface UserAuthApi {

    @GetMapping("/api/userAuth/queryUserRoles")
    Set<String> queryUserRoles(@RequestParam("username") String username);

    @GetMapping("/api/userAuth/queryUserAuths")
    Set<String> queryUserAuths(@RequestParam("username") String username);

    @GetMapping("/api/userAuth/queryDataRule")
    String queryUserDataRule(@RequestParam String permCode);
}
