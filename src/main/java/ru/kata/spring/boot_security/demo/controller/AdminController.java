package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;
    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    @GetMapping(value = "")
    public  String  index(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin";
    }

    @GetMapping("/user/{id}")
    public String showUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user";
    }
    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("user", new User());
        return "new";
    }

//    @PostMapping("/new")
//    public String create(@ModelAttribute("user") User user, BindingResult result, Model model ) {
//        if (result.hasErrors()) {
//            return "newUser";
//        }
//        userService.saveUser(user);
//        model.addAttribute("user", user);
//        return "redirect:/admin";
//    }
//
//    @GetMapping("/{id}/edit")
//    public String edit(Model model, @PathVariable("id")Long id) {
//        model.addAttribute("user", userService.getUserById(id));
//        return "edit";
//    }


    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") User user) {
        if(user.getRoles() == null || user.getRoles().isEmpty()){
            User newUser = userService.getUserById(user.getId());
            user.setRoles(newUser.getRoles());
        }
        userService.updateUser(user);

        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.removeUserById(id);
        return "redirect:/admin";
    }



    @PostMapping("add")
    public String create(@ModelAttribute("user") User user,
                         @RequestParam("rolesList") ArrayList<Long> roles) {
        user.setRoles(roleService.getListRoleById(roles));
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PatchMapping("/{id}/")
    public String edit(@ModelAttribute("user") User user, @RequestParam("rolesList")
    List<Long> roles) {
//        if(user.getRoles() == null || user.getRoles().isEmpty()){
//            User newUser = userService.getUserById(user.getId());
//            user.setRoles(newUser.getRoles());
//        }
        user.setRoles(roleService.getListRoleById(roles));
        userService.updateUser(user);
        return "redirect:/admin";

    }


}
