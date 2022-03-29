/***********************************************************
 * @Description : 
 * @author      : 梁山广(Laing Shan Guang)
 * @date        : 2019-05-14 08:26
 * @email       : liangshanguang2@gmail.com
 ***********************************************************/
package kfgs.classify_auxiliary.repository;

import kfgs.classify_auxiliary.entity.QuestionLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionLevelRepository extends JpaRepository<QuestionLevel, Integer> {
}
