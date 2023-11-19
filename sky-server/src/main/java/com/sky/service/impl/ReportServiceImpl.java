package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;


    /**
     * 统计指定区间内的营业额数据
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate beginTime, LocalDate endTime) {
        //当前集合用于存放begin到end范围内的日期
        ArrayList<LocalDate> dateList = new ArrayList<>();

        dateList.add(beginTime);

        while (!beginTime.equals(endTime)) {
            //计算日期
            beginTime = beginTime.plusDays(1);
            dateList.add(beginTime);
        }

        //存放每天的营业额
        ArrayList<Object> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询date日期对应的营业额数据，当日已完成订单的金额合计
            LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

            HashMap<Object, Object> hashMap = new HashMap<>();
            hashMap.put("begin", begin);
            hashMap.put("end", end);
            hashMap.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(hashMap);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);


        }


        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();

        return turnoverReportVO;
    }

    /**
     * 统计指定区间内的用户数据
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate beginTime, LocalDate endTime) {
        //当前集合用于存放begin到end范围内的日期
        ArrayList<LocalDate> dateList = new ArrayList<>();

        dateList.add(beginTime);

        while (!beginTime.equals(endTime)) {
            //计算日期
            beginTime = beginTime.plusDays(1);
            dateList.add(beginTime);
        }

        ArrayList<Integer> newUserList = new ArrayList<>();//存放新增用户数量
        ArrayList<Integer> totalUserList = new ArrayList<>();//存放总用户数量

        for (LocalDate date : dateList) {
            HashMap<Object, Object> hashMap = new HashMap<>();

            LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
            //总用户数量
            hashMap.put("end", end);
            Integer totalUser = userMapper.countByMap(hashMap);

            //新增用户数量
            hashMap.put("begin", begin);
            Integer newUser = userMapper.countByMap(hashMap);

            totalUserList.add(totalUser);
            newUserList.add(newUser);


        }

        //封装结果数据并返回
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }
}
