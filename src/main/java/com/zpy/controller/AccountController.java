package com.zpy.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zpy.pojo.*;
import com.zpy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private FoodService foodService;
    @Value("${location}")
    private String location;
    
    //跳转到登录页面
    @GetMapping("/toLogin")
    public String toLogin(){
        return "index";
    }

    //跳转到注册页面
    @GetMapping("/toRegister")
    public String toRegister(){
        return "register";
    }
    
    //跳转到主页面
    @RequestMapping("/toDashboard")
    public String toDashboard(){
        return "foodMainMenu";
    }

    //登录
    @RequestMapping("/adminlogin")
    public String login(String userName, String userPwd, Model model, HttpSession session) {
        //判断是否是管理员，如果是进行判断
        boolean login = accountService.login(userName, userPwd);
        if (login) {
            //查询用户名，并将用户名放入session中
            QueryWrapper<User>qw=new QueryWrapper<>();
            //获取前端传来的用户名和密码
            User one = accountService.getOne(qw);
            session.setAttribute("currentUser", userName);
            session.setAttribute("password",userPwd);//这里保存的是没加密的密码
            session.setAttribute("email",one.getEmail());
            session.setAttribute("image",one.getImage());
            //进入主页面
            return "foodMainMenu";
        } else {
            //如果不是则提示用户名或密码错误
            model.addAttribute("msg", "用户名或密码错误！");
            return "index";
        }
    }

    //注册
    @RequestMapping("/register")
    public String register(String userName, String userPwd,String confirmPwd, Model model) {
        if (!userPwd.equals(confirmPwd)) {
            model.addAttribute("msg", "输入密码不一致");
            return "user-register";
        }

        // 检查用户名是否已经存在于数据库中
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_name", userName);
        Customer existingCustomer = customerService.getOne(queryWrapper);
        
        if (existingCustomer != null) {
            model.addAttribute("msg", "用户名已存在，请选择其他用户名");
            return "user-register";
        }

        // 创建新客户对象并保存到数据库
        Customer customer = new Customer();
        customer.setCustomerName(userName);
        customer.setPassword(DigestUtil.md5Hex(userPwd)); // 加密密码
        customerService.save(customer);
        
        model.addAttribute("msg", "注册成功，请登录");
        return "user-login";
    }

    //跳转到修改密码的页面
    @RequestMapping("pwd")
    public String preUpdate() {
        return "profile-admin";
    }

    //跳转到登录页面
    @RequestMapping("login")
    public String index() {
        return "index";
    }
    
    @RequestMapping("toUserLogin")
    public String userLogin() {
        return "user-login";
    }
    
    @RequestMapping("toUserRegister")
    public String userRegister() {
        return "user-register";
    }

    //用户登录
    @PostMapping("userLogin")
    public String user(String username,String password,Model model,HttpSession session) {
        // 标记用户名和密码是否错误
        boolean isUsernameWrong = false;
        boolean isPasswordWrong = false;

        // 根据用户名查询用户信息
        QueryWrapper<Customer> qw = new QueryWrapper<>();
        qw.eq("customer_name", username);
        Customer customer = customerService.getOne(qw);

        if (customer == null) {
            isUsernameWrong = true;
        } else {
            // 对输入的密码进行加密处理
            String encryptedPassword = DigestUtil.md5Hex(password);
            if (!encryptedPassword.equals(customer.getPassword())) {
                isPasswordWrong = true;
            }
        }

        // 统一判断并设置错误提示信息
        if (isUsernameWrong || isPasswordWrong) {
            if (isUsernameWrong && isPasswordWrong) {
                model.addAttribute("msg", "用户名和密码都不正确！");
            } else if (isUsernameWrong) {
                model.addAttribute("msg", "用户名不存在！");
            } else {
                model.addAttribute("msg", "密码错误！");
            }
            return "user-login";
        } else {
            // 登录成功，设置session并跳转到用户首页
            session.setAttribute("currentUser", username);
            session.setAttribute("userId", customer.getId());
            
            // 查询店铺和食物列表
            List<Store> storeList = storeService.list();
            List<Food> foodList = foodService.list();
            
            model.addAttribute("storeList", storeList);
            model.addAttribute("foodList", foodList);
            
            return "user-index";
        }
    }

    //用户修改密码
    @PostMapping("pwdUser")
    public String updatePwd(String userPwd, String newPwd, Model model, HttpSession session) {
        String currentUser = (String) session.getAttribute("currentUser");
        if (currentUser != null) {
            QueryWrapper<Customer> qw = new QueryWrapper<>();
            qw.eq("customer_name", currentUser);
            Customer customer = customerService.getOne(qw);
            
            if (customer != null && DigestUtil.md5Hex(userPwd).equals(customer.getPassword())) {
                customer.setPassword(DigestUtil.md5Hex(newPwd));
                customerService.updateById(customer);
                model.addAttribute("msg", "密码修改成功！");
            } else {
                model.addAttribute("msg", "原密码错误！");
            }
        }
        return "user-profile";
    }

    //退出登录
    @RequestMapping("logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "index";
    }

    //用户退出登录
    @RequestMapping("userLogout")
    public String userLogout(HttpSession session){
        session.invalidate();
        return "user-login";
    }

    //跳转到统计页面
    @RequestMapping("count")
    public String count(){
        return "chart_count";
    }
    
    @RequestMapping("total")
    public String count1(){
        return "chart_total";
    }

    //跳转到个人资料页面
    @RequestMapping("profile")
    public String profile(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        String currentUser = (String) session.getAttribute("currentUser");
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("user_name", currentUser);
        User user = accountService.getOne(qw);
        model.addAttribute("user", user);
        return "profile-admin";
    }

    //跳转到用户个人资料页面
    @RequestMapping("userProfile")
    public String userProfile(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        String currentUser = (String) session.getAttribute("currentUser");
        QueryWrapper<Customer> qw = new QueryWrapper<>();
        qw.eq("customer_name", currentUser);
        Customer customer = customerService.getOne(qw);
        model.addAttribute("customer", customer);
        return "user-profile";
    }

    //更新管理员资料
    @RequestMapping("updateAdminProfile")
    public String updateProfile(User user){
        accountService.updateById(user);
        return "redirect:/profile";
    }

    //更新用户资料
    @RequestMapping("updateUserProfile")
    public String updateReaderProfile(Customer customer, MultipartFile file){
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
        customerService.updateById(customer);
        return "redirect:/userProfile";
    }

    //修改密码
    @RequestMapping("updatePwd")
    public String updatePwd(String userPwd,String newPwd,String confirm ,HttpSession session,Model model ){
        if (!newPwd.equals(confirm)) {
            model.addAttribute("msg", "两次输入的新密码不一致！");
            return "profile-admin";
        }
        
        String currentUser = (String) session.getAttribute("currentUser");
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("user_name", currentUser);
        User user = accountService.getOne(qw);
        
        if (user != null && DigestUtil.md5Hex(userPwd).equals(user.getPassword())) {
            user.setPassword(DigestUtil.md5Hex(newPwd));
            accountService.updateById(user);
            model.addAttribute("msg", "密码修改成功！");
        } else {
            model.addAttribute("msg", "原密码错误！");
        }
        
        model.addAttribute("user", user);
        return "profile-admin";
    }

    //跳转到首页
    @RequestMapping("home")
    public String home(Model model){
        List<Store> storeList = storeService.list();
        List<Food> foodList = foodService.list();
        model.addAttribute("storeList", storeList);
        model.addAttribute("foodList", foodList);
        return "user-index";
    }
}