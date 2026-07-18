package cn.edu.models.users.service;

import cn.edu.models.users.pojo.vo.reqvo.LoginReq;
import cn.edu.models.users.pojo.vo.resvo.LoginRes;

public interface SysUserService {

    LoginRes login(LoginReq req);
}