package com.yutong.modules.demo.controller;



import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.*;

@Slf4j
@Api(tags="菜单")
@RestController
@RequestMapping("/sys/safetySysPerm")
public class getCatchController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();


    @ApiOperation(value = "查询缓存", notes = "查询缓存")
    @GetMapping(value = "/getRedisCatch")
    public Result<?> getRedisCatch(@RequestParam("id") String id){
        Map<String, Object> resultMap = new HashMap<>();

        try {
            // 1. 检查key是否存在
            boolean exists = redisUtil.hasKey(id);
            resultMap.put("key存在性", exists);

            if (!exists) {
                return Result.OK("缓存不存在，请先访问需要权限的接口触发缓存");
            }

            // 2. 获取过期时间
            long expire = redisUtil.getExpire(id);
            resultMap.put("过期时间(秒)", expire);

            // 3. 使用RedisTemplate原生API读取字节并反序列化
            byte[] bytes = (byte[]) redisTemplate.execute((org.springframework.data.redis.core.RedisCallback<byte[]>) connection -> {
                return connection.get(id.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            });

            if (bytes != null && bytes.length > 0) {
                try {
                    java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(bytes);
                    java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
                    Object obj = ois.readObject();
                    ois.close();

                    resultMap.put("缓存类型", obj.getClass().getName());

                    // 如果是Shiro的SimpleAuthorizationInfo对象
                    if (obj instanceof org.apache.shiro.authz.SimpleAuthorizationInfo) {
                        org.apache.shiro.authz.SimpleAuthorizationInfo authInfo =
                                (org.apache.shiro.authz.SimpleAuthorizationInfo) obj;

                        Map<String, Object> permissionData = new HashMap<>();
                        permissionData.put("角色列表", authInfo.getRoles());
                        permissionData.put("字符串权限列表", authInfo.getStringPermissions());
                        permissionData.put("对象权限列表", authInfo.getObjectPermissions());

                        resultMap.put("权限信息", permissionData);
                    } else {
                        resultMap.put("缓存对象", obj.toString());
                    }
                } catch (Exception e) {
                    resultMap.put("反序列化失败", e.getMessage());
                    resultMap.put("失败原因", "可能是JDK版本不兼容或类路径问题");
                }
            } else {
                resultMap.put("提示", "缓存数据为空");
            }

            return Result.OK(resultMap);

        } catch (Exception e) {
            log.error("查询缓存失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }



    @ApiOperation(value = "删除缓存", notes = "删除缓存")
    @GetMapping(value = "/delRedisCatch")
    public Result<?> delRedisCatch(@RequestParam("id") String id){
        redisUtil.del(id);
        return Result.OK(redisUtil.get(id));
    }


}
