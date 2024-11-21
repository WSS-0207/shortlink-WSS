package org.myproject.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.myproject.shortlink.admin.common.convention.result.Result;
import org.myproject.shortlink.admin.common.convention.result.Results;
import org.myproject.shortlink.admin.dto.req.GroupReqDTO;
import org.myproject.shortlink.admin.dto.req.GroupSortReqDTO;
import org.myproject.shortlink.admin.dto.req.GroupUpdateReqDTO;
import org.myproject.shortlink.admin.dto.resp.GroupRespDTO;
import org.myproject.shortlink.admin.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /*
    *
    * */
    @PostMapping("/api/shortlink/admin/v1/group")
    public Result<Void> saveGroup(@RequestBody GroupReqDTO groupParam) {
        groupService.saveGroup(groupParam);
        return Results.success();
    }

    /*
    * 查询分组
    * */
    @GetMapping("/api/shortlink/admin/v1/group")
    public Result<List<GroupRespDTO>> listGroup(){
        return Results.success(groupService.listGroup());
    }

    /*
    * 更新分组
    * */
    @PutMapping("/api/shortlink/admin/v1/group")
    public Result<Void> updateGroup(@RequestBody GroupUpdateReqDTO requestParam){
        groupService.updateGroup(requestParam);
        return Results.success();
    }

    /*
    * 删除短连接分组
    * */
    @DeleteMapping("/api/shortlink/admin/v1/group")
    public Result<Void> removeGroup(@RequestParam("gid") String groupId){
        groupService.removeGroup(groupId);
        return Results.success();
    }

    @PostMapping("/api/shortlink/admin/v1/group/sort")
    public Result<Void> sortGroup(@RequestBody List<GroupSortReqDTO> groupSortReqDTOList){
        groupService.sortGroup(groupSortReqDTOList);
        return Results.success();
    }
}
