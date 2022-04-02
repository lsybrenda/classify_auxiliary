/***********************************************************
 * @Description : 用户服务
 * @author      : 梁山广(Laing Shan Guang)
 * @date        : 2019-05-17 08:03
 * @email       : liangshanguang2@gmail.com
 ***********************************************************/
package kfgs.classify_auxiliary.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import kfgs.classify_auxiliary.dto.RegisterDTO;
import kfgs.classify_auxiliary.entity.Action;
import kfgs.classify_auxiliary.entity.Page;
import kfgs.classify_auxiliary.entity.Role;
import kfgs.classify_auxiliary.entity.User;
import kfgs.classify_auxiliary.enums.LoginTypeEnum;
import kfgs.classify_auxiliary.enums.RoleEnum;
import kfgs.classify_auxiliary.qo.LoginQo;
import kfgs.classify_auxiliary.repository.ActionRepository;
import kfgs.classify_auxiliary.repository.PageRepository;
import kfgs.classify_auxiliary.repository.RoleRepository;
import kfgs.classify_auxiliary.repository.UserRepository;
import kfgs.classify_auxiliary.service.UserService;
import kfgs.classify_auxiliary.utils.JwtUtils;
import kfgs.classify_auxiliary.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    ActionRepository actionRepository;


    @Override
    public User register(RegisterDTO registerDTO) {
        try {
            User user = new User();
            user.setUserId(registerDTO.getId());
            user.setUserUsername(registerDTO.getUsername());
            user.setUserNickname(registerDTO.getId()+"-"+registerDTO.getUsername());
            // 这里还需要进行加密处理，后续解密用Base64.decode()
            user.setUserPassword(Base64.encode(registerDTO.getPassword()));
            // 默认设置为普通用户身份，需要管理员身份的话需要管理员修改
            user.setUserRoleId(RoleEnum.USER.getId());
            // 设置头像图片地址, 先默认一个地址，后面用户可以自己再改
            String defaultAvatar = "http://d.lanrentuku.com/down/png/1904/business_avatar/8_avatar_2754583.png";
            user.setUserAvatar(defaultAvatar);
            // 设置描述信息，随便设置段默认的
            user.setUserDescription("welcome to classify_auxiliary system");
            // 需要验证这个邮箱是不是已经存在：数据字段已经设置unique了，失败会异常地
            user.setUserEmail(registerDTO.getEmail());
            // 需要验证座机号是否已经存在：数据字段已经设置unique了，失败会异常地
            //user.setUserPhone(registerDTO.getPhone());
            user.setUserPhone("0000");
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            // 删除位
            user.setUserIsDeleted((byte) 0);
            userRepository.save(user);
            System.out.println(user);
            return user;
        } catch (Exception e) {
            e.printStackTrace(); // 用户已经存在
            // 出异常，返回null，表示注册失败
            return null;
        }
    }

    @Override
    public String login(LoginQo loginQo) {
        User user;
        if (LoginTypeEnum.USERID.getType().equals(loginQo.getLoginType())){
            //登陆者用的是用户id
            user = userRepository.findByUserId(loginQo.getUserInfo());
        }
        if (LoginTypeEnum.USERNAME.getType().equals(loginQo.getLoginType())) {
            // 登陆者用地是用户名
            user = userRepository.findByUserUsername(loginQo.getUserInfo());
        } else {
            // 登陆者用地是邮箱
            user = userRepository.findByUserEmail(loginQo.getUserInfo());
        }
        if (user != null && user.getUserIsDeleted() == 0) {
            // 如果user不是null且未删除即能找到，才能验证用户名和密码
            // 数据库存的密码
            String passwordDb = Base64.decodeStr(user.getUserPassword());
            // 用户请求参数中的密码
            String passwordQo = loginQo.getPassword();
            System.out.println(passwordDb);
            System.out.println(passwordQo);
            if (passwordQo.equals(passwordDb)) {
                // 如果密码相等地话说明认证成功,返回生成的token，有效期为一天
                return JwtUtils.genJsonWebToken(user);
            }
        }
        return null;
    }

    @Override
    public int deleteById(String userId) {
        User user = userRepository.findByUserId(userId);
        Byte is_deleted = user.getUserIsDeleted();
        is_deleted = (byte)1; //不是真的删除，置为不可用
        user.setUserIsDeleted(is_deleted);
        userRepository.save(user);
        return userRepository.findByUserId(userId).getUserIsDeleted();
    }

    @Override
    public UserVo getUserInfo(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        UserVo userVo = new UserVo();
        assert user != null;
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }

    @Override
    public UserInfoVo getInfo(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        assert user != null;
        UserInfoVo userInfoVo = new UserInfoVo();
        // 1.尽可能的拷贝属性
        BeanUtils.copyProperties(user, userInfoVo);
        Integer roleId = user.getUserRoleId();
        Role role = roleRepository.findById(roleId).orElse(null);
        assert role != null;
        String roleName = role.getRoleName();

        // 2.设置角色名称
        userInfoVo.setRoleName(roleName);

        // 3.设置当前用户的角色细节
        RoleVo roleVo = new RoleVo();
        BeanUtils.copyProperties(role, roleVo);

        // 4.设置角色的可访问页面
        String rolePageIds = role.getRolePageIds();
        String[] pageIdArr = rolePageIds.split("-");
        List<PageVo> pageVoList = new ArrayList<>();
        for (String pageIdStr : pageIdArr) {
            // 获取页面的id
            Integer pageId = Integer.parseInt(pageIdStr);

            // 4.1 向Role中添加Page
            Page page = pageRepository.findById(pageId).orElse(null);
            PageVo pageVo = new PageVo();
            BeanUtils.copyProperties(page, pageVo);

            // 4.2 向Page中添加action
            List<ActionVo> actionVoList = new ArrayList<>();
            String actionIdsStr = page.getActionIds();
            String[] actionIdArr = actionIdsStr.split("-");
            for (String actionIdStr : actionIdArr) {
                Integer actionId = Integer.parseInt(actionIdStr);
                Action action = actionRepository.findById(actionId).orElse(null);
                ActionVo actionVo = new ActionVo();
                assert action != null;
                BeanUtils.copyProperties(action, actionVo);
                actionVoList.add(actionVo);
            }
            // 设置actionVoList到pageVo中，然后把pageVo加到pageVoList中
            pageVo.setActionVoList(actionVoList);
            // 设置pageVoList，下面再设置到RoleVo中
            pageVoList.add(pageVo);
        }
        // 设置PageVo的集合到RoleVo中
        roleVo.setPageVoList(pageVoList);
        // 最终把PageVo设置到UserInfoVo中，这样就完成了拼接
        userInfoVo.setRoleVo(roleVo);
        return userInfoVo;
    }

    @Override
    public List<UserInfoVo> getUserAll() {
        List<User> userList = userRepository.findAllByUserIsDeleted((byte)0);
        return getUserVos(userList);
    }

    private List<UserInfoVo> getUserVos(List<User> list){
        /*需要自定义的用户信息列表
        id,username,nickname,email,phone,role
        */
        List<UserInfoVo> userInfoVoList = new ArrayList<>();
        for (User user : list){
            UserInfoVo userInfoVo = new UserInfoVo();
            userInfoVo.setUserId(user.getUserId());
            userInfoVo.setUserAvatar(user.getUserAvatar());
            userInfoVo.setUserUsername(user.getUserUsername());
            userInfoVo.setUserNickname(user.getUserNickname());
            userInfoVo.setUserEmail(user.getUserEmail());
            userInfoVo.setUserPhone(user.getUserPhone());
            userInfoVo.setRoleName(user.getUserRoleId() == 1 ? "管理员":"用户");
            userInfoVoList.add(userInfoVo);
        }
        return userInfoVoList;
    }
}
