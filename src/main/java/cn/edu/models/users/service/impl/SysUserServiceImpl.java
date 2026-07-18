package cn.edu.models.users.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.edu.commons.apires.CommonErrorCode;
import cn.edu.commons.constants.RedisKeyConstants;
import cn.edu.commons.exception.BusinessException;
import cn.edu.commons.jwt.JwtSession;
import cn.edu.commons.redis.RedisUtil;
import cn.edu.models.users.mapper.SysUserMapper;
import cn.edu.models.users.pojo.entity.SysUserEntity;
import cn.edu.models.users.pojo.vo.reqvo.LoginReq;
import cn.edu.models.users.pojo.vo.resvo.LoginRes;
import cn.edu.models.users.pojo.vo.resvo.UserDetailRes;
import cn.edu.models.users.service.SysUserService;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.RegisteredPayload;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl implements SysUserService {
    private static final long TOKEN_EXPIRE_DAYS = 7L;
    private static final long TOKEN_EXPIRE_MILLIS = TimeUnit.DAYS.toMillis(TOKEN_EXPIRE_DAYS);

    private final SysUserMapper sysUserMapper;
    private final RedisUtil redisUtil;

    public SysUserServiceImpl(SysUserMapper sysUserMapper, RedisUtil redisUtil) {
        this.sysUserMapper = sysUserMapper;
        this.redisUtil = redisUtil;
    }

    @Override
    public LoginRes login(LoginReq req) {
        //用户登录认证校验
        //根据用户名查询用户信息
        SysUserEntity sysUserEntity = sysUserMapper.selectByUsername(req.getUsername());
        // 判断用户是否存在
        if (sysUserEntity == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND);
        }
        // 判断用户状态
        if (!sysUserEntity.getStatus().equals("ACTIVE")) {
            throw new BusinessException(CommonErrorCode.ACCOUNT_NOT_ACTIVE);
        }
        // 判断用户密码是否正确
        if (!sysUserEntity.getPassword().equals(req.getPassword())) {
            throw new BusinessException(CommonErrorCode.PASSWORD_ERROR);
        }

        // 同一用户已有有效登录会话时，直接返回原 token，不重复生成。
        String userTokenKey = RedisKeyConstants.jwtUserKey(sysUserEntity.getId());
        String cachedToken = redisUtil.get(userTokenKey);
        if (cachedToken != null && !cachedToken.isBlank()) {
            String tokenCacheKey = RedisKeyConstants.jwtTokenKey(cachedToken);
            JwtSession cachedSession = redisUtil.get(tokenCacheKey);
            if (isValidSession(cachedToken, cachedSession, sysUserEntity.getId())) {
                return buildLoginRes(cachedToken, cachedSession.getSignSecret(), sysUserEntity);
            }

            // 索引或会话已失效，清理后重新签发。
            redisUtil.delete(tokenCacheKey);
            redisUtil.delete(userTokenKey);
        }

        // 每次登录生成独立的 tokenId 和签名密钥。
        String tokenId = IdUtil.fastSimpleUUID();
        String signSecret = IdUtil.fastSimpleUUID();
        Date now = new Date();
        Map<String, Object> payload = new HashMap<>();
        payload.put(RegisteredPayload.SUBJECT, sysUserEntity.getUsername());
        payload.put(RegisteredPayload.JWT_ID, tokenId);
        payload.put(RegisteredPayload.ISSUED_AT, now);
        payload.put(RegisteredPayload.EXPIRES_AT, new Date(now.getTime() + TOKEN_EXPIRE_MILLIS));
        payload.put("userId", sysUserEntity.getId());
        payload.put("username", sysUserEntity.getUsername());
        String token = JWTUtil.createToken(payload, signSecret.getBytes(StandardCharsets.UTF_8));

        // Redis 的有效期与 JWT 有效期保持一致，便于主动注销和会话失效控制。
        JwtSession session = new JwtSession(sysUserEntity.getId(), sysUserEntity.getUsername(), signSecret);
        redisUtil.set(RedisKeyConstants.jwtTokenKey(token), session,
                TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        redisUtil.set(userTokenKey, token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);

        return buildLoginRes(token, signSecret, sysUserEntity);
    }

    private boolean isValidSession(String token, JwtSession session, Long userId) {
        if (session == null || session.getSignSecret() == null
                || !userId.equals(session.getUserId())) {
            return false;
        }
        try {
            byte[] secret = session.getSignSecret().getBytes(StandardCharsets.UTF_8);
            return JWTUtil.verify(token, secret)
                    && JWTUtil.parseToken(token).setKey(secret).validate(0);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private LoginRes buildLoginRes(String token, String signSecret, SysUserEntity sysUserEntity) {
        LoginRes loginRes = new LoginRes();
        loginRes.setToken(token);
        loginRes.setSignSecret(signSecret);
        UserDetailRes userDetailRes = new UserDetailRes();
        // 将用户信息复制到用户详情响应对象中
        BeanUtil.copyProperties(sysUserEntity, userDetailRes);
        loginRes.setUserDetailRes(userDetailRes);
        return loginRes;
    }
}
