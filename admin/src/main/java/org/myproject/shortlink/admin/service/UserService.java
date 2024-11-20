package org.myproject.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.myproject.shortlink.admin.dao.entity.UserDO;
import org.myproject.shortlink.admin.dto.req.UserLoginReqDTO;
import org.myproject.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.myproject.shortlink.admin.dto.req.UserUpdateReqDTO;
import org.myproject.shortlink.admin.dto.resp.UserLoginRespDTO;
import org.myproject.shortlink.admin.dto.resp.UserRespDTO;

/*
* 用户接口层
* */
public interface UserService extends IService<UserDO> {
    /*
    * 根据用户名查找用户信息
    * @param username 用户名
    * @return 用户信息
    * */
    UserRespDTO getUserByUsername(String username);

    /*
    * 查询用户是否存在
    * @param username 用户名
    * @return true/false
    * */
    Boolean hasUsername(String username);


    /*
    * 用户注册
    * @param registerParam 注册参数
    * */
    void register(UserRegisterReqDTO registerParam);

    /*
    * 更新用户信息
    * @param updateParam 更新参数
    * */
    void update(UserUpdateReqDTO updateParam);

    /*
    * 用户登录
    * @param loginParam 登录参数
    * @return 登录结果
    * */
    UserLoginRespDTO login(UserLoginReqDTO loginParam);

    /*
    * 检查用户是否登录
    * @param username 用户名
    * @param token 登录凭证
    * */
    Boolean checkLogin(String username, String token);

    /*
    * 退出登录
    * */
    void logout(String username, String token);
}
