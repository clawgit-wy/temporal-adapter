package com.yutong.safety.web.controller.web.tree;


import com.yutong.safety.core.handler.tree.TreeHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(tags = "资源树形接口")
@Controller
@RequestMapping("/web/safety/tree")
public class TreeController {
    @Autowired
    private TreeHandler treeHandler;

    /**
     * 获取全部树形数据
     * @param dt
     * @return
     */
    @ApiOperation(value = "获取全部树形数据")
    @RequestMapping("/getZTreeRoot")
    @ResponseBody
    public Object getZTreeRoot(int dt){
        return treeHandler.getZTreeRoot(dt);
    }

    /**
     * ztree
     * @return
     */
    @ApiOperation(value = "获取树形数据")
    @RequestMapping("/getZTree")
    @ResponseBody
    public Object getZTree() {
        return treeHandler.getZTree();
    }
}
