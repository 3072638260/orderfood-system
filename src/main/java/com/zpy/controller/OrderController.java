package com.zpy.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zpy.pojo.*;
import com.zpy.service.CustomerService;
import com.zpy.service.FoodService;
import com.zpy.service.OrderService;
import com.zpy.service.ProductService;
import com.zpy.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("order")
//订单管理
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;
    @Autowired
    private FoodService foodService;

    //查询所有订单
    @RequestMapping("/listOrder")
    public String list(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                       @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model,
                       Food food){
        if (pageNum<=0||pageNum.equals("")|| pageNum==null){
            pageNum=1;
        }
        if (pageSize<=0||pageSize.equals("")|| pageSize==null){
            pageSize=10;
        }

        Integer id = food.getId();
        PageHelper.startPage(pageNum,pageSize);
        PageInfo<Order> list = orderService.listOrder(id);
        model.addAttribute("pageInfo",list);
        return "order-list";
    }

    //跳转到添加订单页面
    @RequestMapping("preSaveOrder")
    public String preBorrow(Model model) {
        List<Food> foodList = foodService.list(null);
        List<Customer> customerList = customerService.list(null);
        model.addAttribute("customerList",customerList);
        model.addAttribute("foodList",foodList);
        return "order-save";
    }

    //添加订单
    @RequestMapping("saveOrder")
    public String borrow(Integer cid,Integer fid,Integer count,Model model){
        Order order = new Order();
        order.setCid(cid);
        order.setFid(fid);
        Food byId = foodService.getById(fid);
        if (count>byId.getStock()){
            model.addAttribute("msg","此商品库存不足，无法购买");
            return "order-save";
        }
        Double total=byId.getPrice()*count;
        order.setTotal(total);
        order.setCount(count);
        order.setIsorder(1);
        order.setStatus(0);
        orderService.save(order);
        
        //减少库存
        byId.setStock(byId.getStock()-count);
        foodService.updateById(byId);
        
        return "redirect:/order/listOrder";
    }

    //跳转到修改订单页面
    @RequestMapping("preUpdateOrder/{id}")
    public String preUpdateOrder(@PathVariable Integer id,Model model){
        Order order = orderService.getById(id);
        model.addAttribute("order",order);
        List<Food> foodList = foodService.list(null);
        List<Customer> customerList = customerService.list(null);
        model.addAttribute("customerList",customerList);
        model.addAttribute("foodList",foodList);
        return "order-update";
    }

    //修改订单
    @RequestMapping("updateOrder")
    public String update(Integer cid, Integer fid, Integer count){
        Order order = new Order();
        order.setCid(cid);
        order.setFid(fid);
        order.setCount(count);
        
        Food byId = foodService.getById(fid);
        Double total=byId.getPrice()*count;
        order.setTotal(total);
        
        orderService.updateById(order);
        return "redirect:/order/listOrder";
    }

    //删除订单
    @RequestMapping("delOrder/{id}")
    public String delReaderBook(@PathVariable("id") Integer id){
        Order order = orderService.getById(id);
        //恢复库存
        Food food = foodService.getById(order.getFid());
        food.setStock(food.getStock() + order.getCount());
        foodService.updateById(food);
        
        orderService.removeById(id);
        return "redirect:/order/listOrder";
    }

    //批量删除订单
    @PostMapping("batchDeleteOrder")
    @ResponseBody
    public String batchDeleteReaderBook(String idList){
        if (StrUtil.isNotBlank(idList)) {
            String[] ids = idList.split(",");
            List<Integer> idsList = new ArrayList<>();
            for (String id : ids) {
                idsList.add(Integer.valueOf(id));
            }
            orderService.removeByIds(idsList);
            return "success";
        }
        return "error";
    }

    //发送邮件通知
    @RequestMapping("sendMessage/{email}/{foodName}/{orderId}")
    public String sendMessage(@PathVariable String email,@PathVariable String foodName,@PathVariable Integer orderId){
        String subject = "订单确认通知";
        String content = "您好！您的订单 #" + orderId + " 中的菜品 " + foodName + " 已确认，感谢您的订购！";
        MailUtils.sendMail(email, subject, content);
        return "redirect:/order/listOrder";
    }
}