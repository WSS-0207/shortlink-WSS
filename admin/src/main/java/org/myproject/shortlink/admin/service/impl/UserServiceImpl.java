package org.myproject.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.myproject.shortlink.admin.common.constant.RedisCacheConstant;
import org.myproject.shortlink.admin.common.convention.exception.ClientException;
import org.myproject.shortlink.admin.common.enums.UserErrorCode;
import org.myproject.shortlink.admin.dao.entity.UserDO;
import org.myproject.shortlink.admin.dao.mapper.UserMapper;
import org.myproject.shortlink.admin.dto.req.UserLoginReqDTO;
import org.myproject.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.myproject.shortlink.admin.dto.req.UserUpdateReqDTO;
import org.myproject.shortlink.admin.dto.resp.UserLoginRespDTO;
import org.myproject.shortlink.admin.dto.resp.UserRespDTO;
import org.myproject.shortlink.admin.service.UserService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
* 用户接口实现层
* */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;


    /*
    * 根据用户名查找用户信息
    * */
    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> eq = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(eq);
        if (userDO == null){
            throw new ClientException(UserErrorCode.USER_NULL);
        }
        UserRespDTO userRespDTO = new UserRespDTO();
        BeanUtils.copyProperties(userDO, userRespDTO);
        return userRespDTO;
    }

    /*
    * 查询用户是否存在
    * */
    @Override
    public Boolean hasUsername(String username) {
//        LambdaQueryWrapper<UserDO> eq = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
//        if(baseMapper.selectOne(eq) != null){
//            throw new ClientException(UserErrorCode.USER_EXIST);
//        }
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO registerParam) {
        if(hasUsername(registerParam.getUsername())){
            throw new ClientException(UserErrorCode.USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER_KEY+registerParam.getUsername());
        try {
            if (lock.tryLock()){
                int flag = baseMapper.insert(BeanUtil.toBean(registerParam, UserDO.class));
                if (flag<1){
                    throw new ClientException(UserErrorCode.USER_SAVE_FAILURE);
                }
                userRegisterCachePenetrationBloomFilter.add(registerParam.getUsername());
                return;
            }
            throw new ClientException(UserErrorCode.USER_NAME_EXIST);
        }finally {
            lock.unlock();
        }
    }

    /*
    * 更新用户信息
    * */
    @Override
    public void update(UserUpdateReqDTO updateParam) {
        // TODO 验证当前用户名是否为登录用户
        LambdaQueryWrapper<UserDO> eq = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, updateParam.getUsername());
        if(baseMapper.selectOne(eq) == null){
            throw new ClientException(UserErrorCode.USER_NULL);
        }
        baseMapper.update(BeanUtil.toBean(updateParam, UserDO.class), eq);
    }

    /*
    * 用户登录
    * */
    @Override
    public UserLoginRespDTO login(UserLoginReqDTO loginParam) {
        LambdaQueryWrapper<UserDO> eq = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, loginParam.getUsername())
                .eq(UserDO::getPassword, loginParam.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(eq);
        if (userDO == null){
            throw new ClientException(UserErrorCode.USER_NULL);
        }
        Boolean hasLogin = stringRedisTemplate.hasKey("login_"+loginParam.getUsername());
        if(hasLogin!=null&&hasLogin){
            throw new ClientException(UserErrorCode.USER_HAS_LOGIN);
        }
        /*
        * Hash
        * Key: Login_username
        * Value:
        * key: token
        * value: userDO(json)
        * */
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put("login_"+loginParam.getUsername(), uuid, JSON.toJSONString(userDO));
        stringRedisTemplate.expire("login_"+loginParam.getUsername(),30L, TimeUnit.DAYS);
        return new UserLoginRespDTO(uuid);
    }

    /*
    * 检查用户是否登录
    * */
    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get("login_" + username, token) !=null;
    }

    /*
    * 用户退出登录
    * */
    @Override
    public void logout(String username, String token) {
        if (checkLogin(username, token)){
            stringRedisTemplate.delete("login_"+username);
            return;
        }
        throw new ClientException(UserErrorCode.USER_NOT_LOGIN);
    }

}
