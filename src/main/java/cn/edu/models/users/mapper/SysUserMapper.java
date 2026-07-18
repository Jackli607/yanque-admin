package cn.edu.models.users.mapper;

import cn.edu.models.users.pojo.entity.SysUserEntity;
import org.apache.ibatis.annotations.Param;

public interface SysUserMapper {

    SysUserEntity selectById(@Param("id") Long id);

    SysUserEntity selectByUsername(@Param("username") String username);

}