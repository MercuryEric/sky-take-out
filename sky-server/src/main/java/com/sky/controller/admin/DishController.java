package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品管理相关接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {

        dishService.saveWithFlavor(dishDTO);

        //清除redis中缓存数据
//        redisTemplate.delete("dish_" + dishDTO.getCategoryId());

        cleanRedisCache("dish_" + dishDTO.getCategoryId());

        return Result.success();

    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        dishService.deleteBatch(ids);

        //将redis中所有菜品缓存数据清除
        cleanRedisCache("dish_*");


        return Result.success();
    }

    /**
     * 根据菜品id查询
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据菜品id查询")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dishVO = dishService.getByIdWithFlavors(id);

        return Result.success(dishVO);

    }


    /**
     * 修改菜品
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        dishService.updateWithFlavors(dishDTO);

        //将redis中所有菜品缓存数据清除
        cleanRedisCache("dish_*");

        return Result.success();

    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(@RequestParam Long categoryId) {
        List<Dish> dishList = dishService.list(categoryId);

        return Result.success(dishList);

    }


    /**
     * 菜品起售停售
     *
     * @param id
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result saleOrNotSale(@RequestParam Long id, @PathVariable Integer status) {
        dishService.saleOrNotSale(status, id);

        //清除redis中所有缓存数据
        cleanRedisCache("dish_*");

        return Result.success();

    }

    /**
     * 清楚redis中缓存菜品数据
     *
     * @param pattern
     */
    private void cleanRedisCache(String pattern) {
        //将redis中所有菜品缓存数据清除
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }


}
