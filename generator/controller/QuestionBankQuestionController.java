package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.annotation.AuthCheck;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.DeleteRequest;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.constant.UserConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.questionBankQuestion .QuestionBankQuestionAddRequest;
import com.yupi.springbootinit.model.dto.questionBankQuestion .QuestionBankQuestionQueryRequest;
import com.yupi.springbootinit.model.dto.questionBankQuestion .QuestionBankQuestionUpdateRequest;
import com.yupi.springbootinit.model.entity.QuestionBankQuestion;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.QuestionBankQuestionVO;
import com.yupi.springbootinit.service.QuestionBankQuestionService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题库题目关联接口
 *
 * @author dadadaguai
 * @from
 */
@RestController
@RequestMapping("/questionBankQuestion ")
@Slf4j
public class QuestionBankQuestionController {

    @Resource
    private QuestionBankQuestionService questionBankQuestion Service;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建题库题目关联
     *
     * @param questionBankQuestion AddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestionBankQuestion(@RequestBody QuestionBankQuestionAddRequest questionBankQuestion AddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankQuestion AddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBankQuestion questionBankQuestion  = new QuestionBankQuestion();
        BeanUtils.copyProperties(questionBankQuestion AddRequest, questionBankQuestion );
        // 数据校验
        questionBankQuestion Service.validQuestionBankQuestion(questionBankQuestion , true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        questionBankQuestion .setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionBankQuestion Service.save(questionBankQuestion );
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionBankQuestionId = questionBankQuestion .getId();
        return ResultUtils.success(newQuestionBankQuestionId);
    }

    /**
     * 删除题库题目关联
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionBankQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionBankQuestion oldQuestionBankQuestion = questionBankQuestion Service.getById(id);
        ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestionBankQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionBankQuestion Service.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题库题目关联（仅管理员可用）
     *
     * @param questionBankQuestion UpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionBankQuestion(@RequestBody QuestionBankQuestionUpdateRequest questionBankQuestion UpdateRequest) {
        if (questionBankQuestion UpdateRequest == null || questionBankQuestion UpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBankQuestion questionBankQuestion  = new QuestionBankQuestion();
        BeanUtils.copyProperties(questionBankQuestion UpdateRequest, questionBankQuestion );
        // 数据校验
        questionBankQuestion Service.validQuestionBankQuestion(questionBankQuestion , false);
        // 判断是否存在
        long id = questionBankQuestion UpdateRequest.getId();
        QuestionBankQuestion oldQuestionBankQuestion = questionBankQuestion Service.getById(id);
        ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionBankQuestion Service.updateById(questionBankQuestion );
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题库题目关联（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionBankQuestionVO> getQuestionBankQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        QuestionBankQuestion questionBankQuestion  = questionBankQuestion Service.getById(id);
        ThrowUtils.throwIf(questionBankQuestion  == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionBankQuestion Service.getQuestionBankQuestionVO(questionBankQuestion , request));
    }

    /**
     * 分页获取题库题目关联列表（仅管理员可用）
     *
     * @param questionBankQuestion QueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionBankQuestion>> listQuestionBankQuestionByPage(@RequestBody QuestionBankQuestionQueryRequest questionBankQuestion QueryRequest) {
        long current = questionBankQuestion QueryRequest.getCurrent();
        long size = questionBankQuestion QueryRequest.getPageSize();
        // 查询数据库
        Page<QuestionBankQuestion> questionBankQuestion Page = questionBankQuestion Service.page(new Page<>(current, size),
                questionBankQuestion Service.getQueryWrapper(questionBankQuestion QueryRequest));
        return ResultUtils.success(questionBankQuestion Page);
    }

    /**
     * 分页获取题库题目关联列表（封装类）
     *
     * @param questionBankQuestion QueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionBankQuestionVO>> listQuestionBankQuestionVOByPage(@RequestBody QuestionBankQuestionQueryRequest questionBankQuestion QueryRequest,
                                                               HttpServletRequest request) {
        long current = questionBankQuestion QueryRequest.getCurrent();
        long size = questionBankQuestion QueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionBankQuestion> questionBankQuestion Page = questionBankQuestion Service.page(new Page<>(current, size),
                questionBankQuestion Service.getQueryWrapper(questionBankQuestion QueryRequest));
        // 获取封装类
        return ResultUtils.success(questionBankQuestion Service.getQuestionBankQuestionVOPage(questionBankQuestion Page, request));
    }

    /**
     * 分页获取当前登录用户创建的题库题目关联列表
     *
     * @param questionBankQuestion QueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionBankQuestionVO>> listMyQuestionBankQuestionVOByPage(@RequestBody QuestionBankQuestionQueryRequest questionBankQuestion QueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankQuestion QueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionBankQuestion QueryRequest.setUserId(loginUser.getId());
        long current = questionBankQuestion QueryRequest.getCurrent();
        long size = questionBankQuestion QueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionBankQuestion> questionBankQuestion Page = questionBankQuestion Service.page(new Page<>(current, size),
                questionBankQuestion Service.getQueryWrapper(questionBankQuestion QueryRequest));
        // 获取封装类
        return ResultUtils.success(questionBankQuestion Service.getQuestionBankQuestionVOPage(questionBankQuestion Page, request));
    }

    /**
     * 编辑题库题目关联（给用户使用）
     *
     * @param questionBankQuestion EditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestionBankQuestion(@RequestBody QuestionBankQuestionEditRequest questionBankQuestion EditRequest, HttpServletRequest request) {
        if (questionBankQuestion EditRequest == null || questionBankQuestion EditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBankQuestion questionBankQuestion  = new QuestionBankQuestion();
        BeanUtils.copyProperties(questionBankQuestion EditRequest, questionBankQuestion );
        // 数据校验
        questionBankQuestion Service.validQuestionBankQuestion(questionBankQuestion , false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = questionBankQuestion EditRequest.getId();
        QuestionBankQuestion oldQuestionBankQuestion = questionBankQuestion Service.getById(id);
        ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestionBankQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionBankQuestion Service.updateById(questionBankQuestion );
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
