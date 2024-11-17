package com.yupi.springbootinit.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.entity.Question;
import com.yupi.springbootinit.model.entity.QuestionBank;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 题库视图
 *
 * @author dadadaguai
 * @from
 */
@Data
public class QuestionBankVO implements Serializable {

  /** id */
  private Long id;

  /** 标题 */
  private String title;

  /** 描述 */
  private String description;

  /** 创建用户 id */
  private Long userId;

  /** 图片 */
  private String picture;

  /** 创建时间 */
  private Date createTime;

  /** 更新时间 */
  private Date updateTime;

  /** 创建用户信息 */
  private UserVO user;

  /** 题目列表 */
  private Page<Question> questionPage;

  /**
   * 封装类转对象
   *
   * @param questionBankVO
   * @return
   */
  public static QuestionBank voToObj(QuestionBankVO questionBankVO) {
    if (questionBankVO == null) {
      return null;
    }
    QuestionBank questionBank = new QuestionBank();
    BeanUtils.copyProperties(questionBankVO, questionBank);
    return questionBank;
  }

  /**
   * 对象转封装类
   *
   * @param questionBank
   * @return
   */
  public static QuestionBankVO objToVo(QuestionBank questionBank) {
    if (questionBank == null) {
      return null;
    }
    QuestionBankVO questionBankVO = new QuestionBankVO();
    BeanUtils.copyProperties(questionBank, questionBankVO);
    return questionBankVO;
  }
}
