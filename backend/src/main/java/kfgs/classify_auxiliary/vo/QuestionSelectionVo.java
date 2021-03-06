/***********************************************************
 * @Description : 前端创建问题时的下拉列表选择
 * @author      : 梁山广(Laing Shan Guang)
 * @date        : 2019-06-03 07:35
 * @email       : liangshanguang2@gmail.com
 ***********************************************************/
package kfgs.classify_auxiliary.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import kfgs.classify_auxiliary.entity.QuestionCategory;
import kfgs.classify_auxiliary.entity.QuestionLevel;
import kfgs.classify_auxiliary.entity.QuestionType;
import lombok.Data;

import java.util.List;

@Data
public class QuestionSelectionVo {
    @JsonProperty("types")
    private List<QuestionType> questionTypeList;

    @JsonProperty("categories")
    private List<QuestionCategory> questionCategoryList;

    @JsonProperty("levels")
    private List<QuestionLevel> questionLevelList;
}
