package cn.edu.models.users.controller;

import cn.edu.commons.apires.ApiResponse;
import cn.edu.models.users.pojo.vo.resvo.LoginRes;
import cn.edu.models.users.pojo.vo.reqvo.LoginReq;
import cn.edu.models.users.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public ApiResponse<LoginRes> login(@Valid @RequestBody LoginReq req) {
        return ApiResponse.success(sysUserService.login(req));
    }
}