package kfgs.classify_auxiliary.qo;
 
/***********************************************************
 * @Description : 文件传输的实体类
 * @author      : 梁山广
 * @date        : 2017/8/19 15:51
 * @version     : V1.0
 ***********************************************************/
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadModel {
    /**
     * 要保存的文件
     */
    private MultipartFile[] files;
    /**
     * 文件要存储的文件夹
     */
    private String dir;
}