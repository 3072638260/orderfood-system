package com.zpy.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Dialect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zpy.pojo.Customer;
import com.zpy.service.CustomerService;
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

//客户管理功能
@Controller
@RequestMapping("customer")
public class CustomerController {
    @Value("${location}")
    private String location;
    @Autowired
    private CustomerService customerService;

    //查询所有用户
    @RequestMapping("listCustomer")
    public String listCustomer(@RequestParam(required = false,defaultValue = "1",value = "pageNum") Integer pageNum,
                               @RequestParam(required = false,defaultValue = "10",value = "pageSize") Integer pageSize, Model model, Customer customer){
        if (pageNum <= 0 || pageNum.equals("") || pageNum == null) {
            pageNum = 1;
        }
        if (pageSize <= 0 || pageSize.equals("") || pageSize == null) {
            pageSize = 10;
        }
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        //模糊查询
        QueryWrapper<Customer> qw=new QueryWrapper<>();
        if (customer.getCustomerName()!=null){
            qw.like("customer_name",customer.getCustomerName());
        }
        List<Customer> list = customerService.list(qw);
        // 使用PageInfo封装查询结果，用于获取分页相关信息
        PageInfo<Customer> pageInfo = new PageInfo<>(list);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("customer", customer);
        return "customer-list";
    }

    //跳转到添加用户页面
    @RequestMapping("preSaveCustomer")
    public String preSaveBook(){
        return "customer-save";
    }

    //添加用户
    @RequestMapping("saveCustomer")
    public String saveBook(Customer customer, MultipartFile file,Model model){
        //对密码进行加密
        customer.setPassword(DigestUtil.md5Hex(customer.getPassword()));
        //处理文件上传
        transFile(customer, file);
        //保存用户
        customerService.save(customer);
        return "redirect:/customer/listCustomer";
    }

    //文件上传处理
    private void transFile(Customer customer, MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename();
                String filePath = location + "/" + fileName;
                File dest = new File(filePath);
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
                file.transferTo(dest);
                customer.setCimage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //跳转到修改用户页面
    @RequestMapping("preUpdateCustomer/{id}")
    public String preUpdateBook(@PathVariable("id")Integer id,Model model){
        Customer customer = customerService.getById(id);
        model.addAttribute("customer", customer);
        return "customer-update";
    }

    //修改用户
    @RequestMapping("updateCustomer")
    public String updateBook(Customer customer){
        //如果密码不为空，则加密
        if (StrUtil.isNotBlank(customer.getPassword())) {
            customer.setPassword(DigestUtil.md5Hex(customer.getPassword()));
        }
        customerService.updateById(customer);
        return "redirect:/customer/listCustomer";
    }

    //删除用户
    @RequestMapping("delCustomer/{id}")
    public String delBook(@PathVariable("id") Integer id){
        customerService.removeById(id);
        return "redirect:/customer/listCustomer";
    }

    //批量删除用户
    @PostMapping("batchDeleteCustomer")
    @ResponseBody
    public String batchDeleteBook(String idList){
        if (StrUtil.isNotBlank(idList)) {
            String[] ids = idList.split(",");
            List<Integer> idsList = new ArrayList<>();
            for (String id : ids) {
                idsList.add(Integer.valueOf(id));
            }
            customerService.removeByIds(idsList);
            return "success";
        }
        return "error";
    }
}