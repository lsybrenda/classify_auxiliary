/***********************************************************
 * @Description : 用户表的操作类
 * @author      : 梁山广(Laing Shan Guang)
 * @date        : 2019-05-14 08:30
 * @email       : liangshanguang2@gmail.com
 ***********************************************************/
package kfgs.classify_auxiliary.repository;

import kfgs.classify_auxiliary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 根据用户id查找到合适的用户
     * @param userid
     * @return
     */
    User findByUserId(String userid);
    /**
     * 根据用户名查找到合适的用户
     *
     * @param username 用户名
     * @return 唯一符合的用户(实际用户名字段已经在数据库设置unique了 ， 肯定只会返回1条)
     */
    User findByUserUsername(String username);

    /**
     * 根据用户邮箱查找合适用户
     *
     * @param email 邮箱
     * @return 唯一符合的用户(实际邮箱字段已经在数据库设置unique了 ， 肯定只会返回1条)
     */
    User findByUserEmail(String email);

    @Query(nativeQuery = true,value ="select * from user where user_is_deleted =?")
    List<User> findAllByUserIsDeleted(@Param("is_deleted") Byte is_deleted);

}
