package com.yupi.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.mapper.QuestionBankQuestionMapper;
import com.yupi.springbootinit.model.dto.questionBankQuestion .QuestionBankQuestionQueryRequest;
import com.yupi.springbootinit.model.entity.QuestionBankQuestion;
import com.yupi.springbootinit.model.entity.QuestionBankQuestionFavour;
import com.yupi.springbootinit.model.entity.QuestionBankQuestionThumb;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.QuestionBankQuestionVO;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.QuestionBankQuestionService;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题库题目关联服务实现
 *
 * @author dadadaguai
 * @from
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param questionBankQuestion 
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion , boolean add) {
        ThrowUtils.throwIf(questionBankQuestion  == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = questionBankQuestion .getTitle();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param questionBankQuestion QueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestion QueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestion QueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankQuestion QueryRequest.getId();
        Long notId = questionBankQuestion QueryRequest.getNotId();
        String title = questionBankQuestion QueryRequest.getTitle();
        String content = questionBankQuestion QueryRequest.getContent();
        String searchText = questionBankQuestion QueryRequest.getSearchText();
        String sortField = questionBankQuestion QueryRequest.getSortField();
        String sortOrder = questionBankQuestion QueryRequest.getSortOrder();
        List<String> tagList = questionBankQuestion QueryRequest.getTags();
        Long userId = questionBankQuestion QueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题库题目关联封装
     *
     * @param questionBankQuestion 
     * @param request
     * @return
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion , HttpServletRequest request) {
        // 对象转封装类
        QuestionBankQuestionVO questionBankQuestion VO = QuestionBankQuestionVO.objToVo(questionBankQuestion );

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionBankQuestion .getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankQuestion VO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long questionBankQuestion Id = questionBankQuestion .getId();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<QuestionBankQuestionThumb> questionBankQuestion ThumbQueryWrapper = new QueryWrapper<>();
            questionBankQuestion ThumbQueryWrapper.in("questionBankQuestion Id", questionBankQuestion Id);
            questionBankQuestion ThumbQueryWrapper.eq("userId", loginUser.getId());
            QuestionBankQuestionThumb questionBankQuestion Thumb = questionBankQuestion ThumbMapper.selectOne(questionBankQuestion ThumbQueryWrapper);
            questionBankQuestion VO.setHasThumb(questionBankQuestion Thumb != null);
            // 获取收藏
            QueryWrapper<QuestionBankQuestionFavour> questionBankQuestion FavourQueryWrapper = new QueryWrapper<>();
            questionBankQuestion FavourQueryWrapper.in("questionBankQuestion Id", questionBankQuestion Id);
            questionBankQuestion FavourQueryWrapper.eq("userId", loginUser.getId());
            QuestionBankQuestionFavour questionBankQuestion Favour = questionBankQuestion FavourMapper.selectOne(questionBankQuestion FavourQueryWrapper);
            questionBankQuestion VO.setHasFavour(questionBankQuestion Favour != null);
        }
        // endregion

        return questionBankQuestion VO;
    }

    /**
     * 分页获取题库题目关联封装
     *
     * @param questionBankQuestion Page
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestion Page, HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankQuestion List = questionBankQuestion Page.getRecords();
        Page<QuestionBankQuestionVO> questionBankQuestion VOPage = new Page<>(questionBankQuestion Page.getCurrent(), questionBankQuestion Page.getSize(), questionBankQuestion Page.getTotal());
        if (CollUtil.isEmpty(questionBankQuestion List)) {
            return questionBankQuestion VOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionBankQuestion VOList = questionBankQuestion List.stream().map(questionBankQuestion  -> {
            return QuestionBankQuestionVO.objToVo(questionBankQuestion );
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionBankQuestion List.stream().map(QuestionBankQuestion::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> questionBankQuestion IdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> questionBankQuestion IdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> questionBankQuestion IdSet = questionBankQuestion List.stream().map(QuestionBankQuestion::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
            // 获取点赞
            QueryWrapper<QuestionBankQuestionThumb> questionBankQuestion ThumbQueryWrapper = new QueryWrapper<>();
            questionBankQuestion ThumbQueryWrapper.in("questionBankQuestion Id", questionBankQuestion IdSet);
            questionBankQuestion ThumbQueryWrapper.eq("userId", loginUser.getId());
            List<QuestionBankQuestionThumb> questionBankQuestion QuestionBankQuestionThumbList = questionBankQuestion ThumbMapper.selectList(questionBankQuestion ThumbQueryWrapper);
            questionBankQuestion QuestionBankQuestionThumbList.forEach(questionBankQuestion QuestionBankQuestionThumb -> questionBankQuestion IdHasThumbMap.put(questionBankQuestion QuestionBankQuestionThumb.getQuestionBankQuestionId(), true));
            // 获取收藏
            QueryWrapper<QuestionBankQuestionFavour> questionBankQuestion FavourQueryWrapper = new QueryWrapper<>();
            questionBankQuestion FavourQueryWrapper.in("questionBankQuestion Id", questionBankQuestion IdSet);
            questionBankQuestion FavourQueryWrapper.eq("userId", loginUser.getId());
            List<QuestionBankQuestionFavour> questionBankQuestion FavourList = questionBankQuestion FavourMapper.selectList(questionBankQuestion FavourQueryWrapper);
            questionBankQuestion FavourList.forEach(questionBankQuestion Favour -> questionBankQuestion IdHasFavourMap.put(questionBankQuestion Favour.getQuestionBankQuestionId(), true));
        }
        // 填充信息
        questionBankQuestion VOList.forEach(questionBankQuestion VO -> {
            Long userId = questionBankQuestion VO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionBankQuestion VO.setUser(userService.getUserVO(user));
            questionBankQuestion VO.setHasThumb(questionBankQuestion IdHasThumbMap.getOrDefault(questionBankQuestion VO.getId(), false));
            questionBankQuestion VO.setHasFavour(questionBankQuestion IdHasFavourMap.getOrDefault(questionBankQuestion VO.getId(), false));
        });
        // endregion

        questionBankQuestion VOPage.setRecords(questionBankQuestion VOList);
        return questionBankQuestion VOPage;
    }

}
