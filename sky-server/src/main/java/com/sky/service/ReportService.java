package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportService {
    /**
     * 统计指定区间内的营业额数据
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate beginTime, LocalDate endTime);

    /**
     * 统计指定区间内的用户数据
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    UserReportVO getUserStatistics(LocalDate beginTime, LocalDate endTime);

    /**
     * 统计指定时间区间内的订单数据
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    OrderReportVO getOrdersStatistics(LocalDate beginTime, LocalDate endTime);
    /**
     * 统计指定时间区间内的销量排名TOP10
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    SalesTop10ReportVO getSalesTop10(LocalDate beginTime, LocalDate endTime);
}
