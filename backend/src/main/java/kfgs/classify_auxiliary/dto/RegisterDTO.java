/***********************************************************
 * @Description : 注册接口参数
 * @author      : 梁山广(Laing Shan Guang)
 * @date        : 2019-05-16 23:40
 * @email       : liangshanguang2@gmail.com
 ***********************************************************/
package kfgs.classify_auxiliary.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    /**
     * 分类员Id
     */
    private String id;
    private String username;
    private String nickname;//用户昵称：id-username
    private String email;
    private String password;
    private String password2;
    //private String phone;
    /**
     * 验证码
     */
    //private String captcha;
}
