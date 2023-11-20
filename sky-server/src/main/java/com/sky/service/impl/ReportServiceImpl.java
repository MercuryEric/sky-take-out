package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 统计指定时间区间内的订单数据
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate beginTime, LocalDate endTime) {
        //当前集合用于存放begin到end范围内的日期
        ArrayList<LocalDate> dateList = new ArrayList<>();

        dateList.add(beginTime);

        while (!beginTime.equals(endTime)) {
            //计算日期
            beginTime = beginTime.plusDays(1);
            dateList.add(beginTime);
        }

        ArrayList<Integer> orderCountList = new ArrayList<>();//存放每天订单总数
        ArrayList<Integer> validOrderCountList = new ArrayList<>();//存放每天有效订单总数

        //遍历dateList集合，查询每天的有效订单数和订单总量
        for (LocalDate date : dateList) {
            HashMap<Object, Object> hashMap = new HashMap<>();

            LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

            //查询每天的订单总数
            Integer orderCount = getOrderCount(begin, end, null);

            //查询每天的有效订单数
            Integer validOrderCount = getOrderCount(begin, end, Orders.COMPLETED);


            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);

        }

        //计算订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        //计算有效订单总数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();


        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {

            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;

        }

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();

        return orderReportVO;
    }

    /**
     * 统计指定时间区间内的销量排名TOP10
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate beginTime, LocalDate endTime) {
        LocalDateTime begin = LocalDateTime.of(beginTime, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endTime, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(begin, end);

        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");
        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();


        return salesTop10ReportVO;
    }


    /**
     * 根据条件统计订单数量
     *
     * @param begin
     * @param end
     * @param status
     * @return
     */

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("begin", begin);
        hashMap.put("end", end);
        hashMap.put("status", status);

        Integer count = orderMapper.countByMap(hashMap);

        return count;


    }


}
