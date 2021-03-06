/***********************************************************
 * @Description : 用户接口
 * @author      : 梁山广(Laing Shan Guang)
 * @date        : 2019-05-17 08:02
 * @email       : liangshanguang2@gmail.com
 ***********************************************************/
package kfgs.classify_auxiliary.service;

import kfgs.classify_auxiliary.dto.RegisterDTO;
import kfgs.classify_auxiliary.entity.User;
import kfgs.classify_auxiliary.qo.LoginQo;
import kfgs.classify_auxiliary.vo.UserInfoVo;
import kfgs.classify_auxiliary.vo.UserVo;

import java.util.List;

public interface UserService {
    /**
     * 注册
     *
     * @param registerDTO 注册参数
     * @return 注册成功后的用户信息
     */
    User register(RegisterDTO registerDTO);

    /**
     * 登录接口，登录成功返回token
     *
     * @param loginQo 登录参数
     * @return 成功返回token，失败返回null
     */
    String login(LoginQo loginQo);

    /**
     * 删除用户
     * @param userId
     * @return
     */
    int deleteById(String userId);

    /**
     * 根据用户id获取用户信息
     *
     * @return 用户实体
     */
    UserVo getUserInfo(String userId);

    /**
     * 获取用户详细信息(主要是权限相关的)
     * @param userId 用户的id
     * @return 用户信息组装的实体
     */
    UserInfoVo getInfo(String userId);

    /**
     * 获取所有人员列表信息
     * @return
     */
    List<UserInfoVo> getUserAll();
}
