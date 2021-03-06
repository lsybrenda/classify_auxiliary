/***********************************************************
 * @Description : 对外REST接口
 * @author      : 梁山广(Laing Shan Guang)
 * @date        : 2019-05-16 23:45
 * @email       : liangshanguang2@gmail.com
 ***********************************************************/
package kfgs.classify_auxiliary.controller;

import kfgs.classify_auxiliary.dto.RegisterDTO;
import kfgs.classify_auxiliary.entity.User;
import kfgs.classify_auxiliary.enums.ResultEnum;
import kfgs.classify_auxiliary.qo.LoginQo;
import kfgs.classify_auxiliary.service.UserService;
import kfgs.classify_auxiliary.vo.ResultVO;
import kfgs.classify_auxiliary.vo.UserInfoVo;
import kfgs.classify_auxiliary.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = "User APIs")
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation("注册")
    ResultVO<User> register(@RequestBody RegisterDTO registerDTO) {
        ResultVO<User> resultVO;
        // 注册信息的完善，还有唯一性校验没(用户名、邮箱和手机号)已经在user表中通过unique来设置了
        User user = userService.register(registerDTO);
        if (user != null) {
            // 注册成功
            resultVO = new ResultVO<>(ResultEnum.REGISTER_SUCCESS.getCode(), ResultEnum.REGISTER_SUCCESS.getMessage(), user);
        } else {
            resultVO = new ResultVO<>(ResultEnum.REGISTER_FAILED.getCode(), ResultEnum.REGISTER_FAILED.getMessage(), null);
        }
        return resultVO;
    }

    @PostMapping("/login")
    @ApiOperation("根据用户名或邮箱登录,登录成功返回token")
    ResultVO<String> login(@RequestBody LoginQo loginQo) { // 这里不用手机号是因为手机号和用户名难以进行格式区分，而用户名和
        // 用户登录
        ResultVO<String> resultVO;
        String token = userService.login(loginQo);
        if (token != null) {
            // 登录成功
            resultVO = new ResultVO<>(ResultEnum.LOGIN_SUCCESS.getCode(), ResultEnum.LOGIN_SUCCESS.getMessage(), token);
        } else {
            // 登录失败
            resultVO = new ResultVO<>(ResultEnum.LOGIN_FAILED.getCode(), ResultEnum.LOGIN_FAILED.getMessage(), null);
        }
        return resultVO;
    }

    @PostMapping("/deleteUserById/{userId}")
    @ApiOperation("根据Id删除用户")
    ResultVO<String> deleteUserById(@PathVariable String userId, HttpServletRequest request){
        ResultVO<String> resultVO;
        // 不是真的删除，只是更改is_deleted状态
        int result = userService.deleteById(userId);
        if (result == 1){
            //删除成功
            resultVO = new ResultVO<>(ResultEnum.DELETE_SUCCESS.getCode(),ResultEnum.DELETE_SUCCESS.getMessage(),null);
        }else {
            resultVO = new ResultVO<>(ResultEnum.DELETE_ERR.getCode(),ResultEnum.DELETE_ERR.getMessage(),null);
        }
        return resultVO;
    }

    @GetMapping("/user-info")
    @ApiOperation("获取用户信息")
    ResultVO<UserVo> getUserInfo(HttpServletRequest request) {
        String userId = (String) request.getAttribute("user_id");
        UserVo userVo = userService.getUserInfo(userId);
        return new ResultVO<>(ResultEnum.GET_INFO_SUCCESS.getCode(), ResultEnum.GET_INFO_SUCCESS.getMessage(), userVo);
    }

    @GetMapping("/info")
    @ApiOperation("获取用户的详细信息，包括个人信息页面和操作权限")
    ResultVO<UserInfoVo> getInfo(HttpServletRequest request) {
        System.out.println("进入/user/info的获取用户信息的接口");
        String userId = (String) request.getAttribute("user_id");
        UserInfoVo userInfoVo = userService.getInfo(userId);
        return new ResultVO<>(ResultEnum.GET_INFO_SUCCESS.getCode(), ResultEnum.GET_INFO_SUCCESS.getMessage(), userInfoVo);
    }

    @GetMapping("/test")
    @ApiOperation("测试接口")
    String test(HttpServletRequest request) {
        // 下面这两个属性都是登录拦截器从token中解析地，当用户名不对或者token过期时是走不到接口内的
        String userId = (String) request.getAttribute("user_id");
        String username = (String) request.getAttribute("username");
        System.out.println("用户id：" + userId);
        System.out.println("用户名：" + username);
        return "用户id：" + userId + "\n用户名：" + username;
    }

    @GetMapping("/all")
    @ApiOperation("获取全部人员信息的列表")
    ResultVO<List<UserInfoVo>> getUserAll() {
        // 需要拼接前端需要的考试列表对象
        ResultVO<List<UserInfoVo>> resultVO;
        try {
            List<UserInfoVo> examVos = userService.getUserAll();
            resultVO = new ResultVO<>(0, "获取全部人员的列表成功", examVos);
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = new ResultVO<>(-1, "获取全部人员的列表失败", null);
        }
        return resultVO;
    }

}
