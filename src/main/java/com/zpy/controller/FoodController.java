package com.zpy.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zpy.pojo.Food;
import com.zpy.pojo.Product;
import com.zpy.pojo.Store;
import com.zpy.service.FoodService;
import com.zpy.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("food")
public class FoodController {
    @Autowired
    private FoodService foodService;

    @Value("${location}")
    private String location;

    @Autowired
    private StoreService storeService;

    //查询所有菜品
    @RequestMapping("listFood")
    public String listProduct(@RequestParam(required = false,defaultValue = "1",value = "pageNum") Integer pageNum,
                              @RequestParam(required = false,defaultValue = "10",value = "pageSize") Integer pageSize, Model model, Food food){
        if (pageNum <= 0 || pageNum.equals("") || pageNum == null) {
            pageNum = 1;
        }
        if (pageSize <= 0 || pageSize.equals("") || pageSize == null) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<Food> qw=new QueryWrapper<>();
        if (food.getFoodName()!=null){
            qw.like("food_name",food.getFoodName());
        }
        if (food.getStore()!=null){
            qw.eq("store",food.getStore());
        }

        List<Food> list = foodService.list(qw);
        PageInfo<Food>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        List<Store> storeList = storeService.list(null);
        model.addAttribute("storeList",storeList);

        return "food-list";
    }

    //跳转到添加商品的页面
    @RequestMapping("preSaveFood")
    public String preSaveProduct(Model model){
        List<Store> list = storeService.list(null);
        model.addAttribute("storeList",list);
        return "food-save";
    }

    //添加菜品
    @RequestMapping("saveFood")
    public String saveBook(Food food, MultipartFile file){
        transFile(food,file);
        boolean save = foodService.save(food);
        return "redirect:/food/listFood";
    }
    
    //文件上传
    private void transFile(Food food, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(index);
        String prefix =System.nanoTime()+"";
        String path=prefix+suffix;
        File file1 = new File(location);
        if (!file1.exists()){
            file1.mkdirs();
        }
        File file2 = new File(file1,path);
        try {
            file.transferTo(file2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        food.setFimage(path);
    }

    //跳转到修改菜品页面
    @RequestMapping("preUpdateFood/{id}")
    public String preUpdateFood(@PathVariable Integer id,Model model){
        Food food = foodService.getById(id);
        model.addAttribute("food",food);
        List<Store> list = storeService.list(null);
        model.addAttribute("storeList",list);
        return "food-update";
    }

    //修改菜品
    @RequestMapping("updateFood")
    public String updateFood(Food food){
        foodService.updateById(food);
        return "redirect:/food/listFood";
    }

    //上架菜品
    @RequestMapping("shangjia/{id}")
    public String shangjia(@PathVariable Integer id){
        Food food = foodService.getById(id);
        food.setStatus(1);
        foodService.updateById(food);
        return "redirect:/food/listFood";
    }

    //下架菜品
    @RequestMapping("xiajia/{id}")
    public String xiajia(@PathVariable Integer id){
        Food food = foodService.getById(id);
        food.setStatus(0);
        foodService.updateById(food);
        return "redirect:/food/listFood";
    }

    //删除菜品
    @RequestMapping("delFood/{id}")
    public String delBook(@PathVariable("id") Integer id){
        foodService.removeById(id);
        return "redirect:/food/listFood";
    }

    //批量删除菜品
    @PostMapping("batchDeleteFood")
    @ResponseBody
    public String batchDeleteBook(String idList){
        if (StrUtil.isNotBlank(idList)) {
            String[] ids = idList.split(",");
            List<Integer> idsList = new ArrayList<>();
            for (String id : ids) {
                idsList.add(Integer.valueOf(id));
            }
            foodService.removeByIds(idsList);
            return "success";
        }
        return "error";
    }
}