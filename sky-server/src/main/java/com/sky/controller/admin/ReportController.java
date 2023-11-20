package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 数据统计相关接口
 */
@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计相关接口")
@Slf4j
public class ReportController {
    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")@RequestParam("begin")
            LocalDate beginTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd")@RequestParam("end")
            LocalDate endTime) {

        return Result.success(reportService.getTurnoverStatistics(beginTime, endTime));


    }


    /**
     * 用户统计
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")@RequestParam("begin")
            LocalDate beginTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd")@RequestParam("end")
            LocalDate endTime) {

        return Result.success(reportService.getUserStatistics(beginTime, endTime));


    }

    /**
     * 订单统计
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")@RequestParam("begin")
            LocalDate beginTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd")@RequestParam("end")
            LocalDate endTime) {

        return Result.success(reportService.getOrdersStatistics(beginTime, endTime));


    }



    /**
     * 销量TOP10统计
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("销量TOP10统计")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd")@RequestParam("begin")
            LocalDate beginTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd")@RequestParam("end")
            LocalDate endTime) {

        return Result.success(reportService.getSalesTop10(beginTime, endTime));


    }



}