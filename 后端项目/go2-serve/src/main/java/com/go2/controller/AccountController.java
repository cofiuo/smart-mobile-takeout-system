package com.go2.controller;

import com.go2.common.Const;
import com.go2.common.Result;
import com.go2.entity.Account;
import com.go2.service.AccountService;
import com.go2.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.go2.common.BaseController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author fish
 * @since 2026-01-01
 */
@RestController
@RequestMapping("/account")
@Slf4j
@CrossOrigin
public class AccountController extends BaseController {
    @Autowired
    private AccountService accountService;

    // 获得所有的account用户数据
    @RequestMapping("/list")
    public Result list() {
        List<Account> list = accountService.list();
        if (list == null) {
            return Result.fail("用户信息加载失败");
        } else {
            return Result.success(list);
        }
    }

    // 检测手机号是否被注册
    @GetMapping("/check/{accountId}")
    public Result check(@PathVariable String accountId) {
        Account account = accountService.getById(accountId);
        if (account == null) {
            return Result.success(20000, "该账户(手机号)可以注册", null);
        } else {
            return Result.fail(Const.PHONE_NUMBER_EXIST, "该账户(手机号)不可注册", null);
        }
    }

    @PostMapping("/login")
    public Result login(String accountId, String password) {
        log.info("手机号为{}用户正在进行登录...", accountId);
        // 查询账户(手机号)在sys_account是否存在
        // MP提供:getById(),根据主键查询对象
        Account account = accountService.getById(accountId);
        if (account == null) {
            return Result.fail(Const.ACCOUNT_NOT_EXIST, "账户(手机号)不存在", null);
        } else {
            String md5Pass = MD5Utils.md5(MD5Utils.inputPassToNewPass(password));
            if (md5Pass.equals(account.getPassword())) {
                // 判断用户状态
                if (account.getStatu() == 0) {
                    return Result.fail(Const.ACCOUNT_STATU_ERR, "账户被禁用或注销，暂不可用", null);
                } else {
                    // 登陆成功
                    return Result.success(account);
                }
            } else {
                return Result.fail(Const.PASSWORD_ERR, "登录密码不正确", null);
            }
        }
    }

    // 注册
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        log.info("用户注册请求，手机号：{}", account.getAccountId());

        // 密码加密
        String rawPassword = account.getPassword();
        String encryptedPassword = MD5Utils.md5(MD5Utils.inputPassToNewPass(rawPassword));

        account.setAccountId(account.getAccountId());
        account.setPassword(encryptedPassword);
        account.setCreated(LocalDateTime.now());
        account.setUpdated(LocalDateTime.now());
        account.setStatu(1);
        // 保存用户
        boolean saveSuccess = accountService.save(account);
        if (saveSuccess) {
            return Result.success("注册成功");
        } else {
            return Result.fail(null);
        }
    }

    // 注销账号，将账户状态改为0
    @PostMapping("/cancel")
    public Result cancelAccount(@RequestBody Account account1) {
        log.info("用户{}申请注销账号", account1.getAccountId());
        // 查询账户是否存在
        Account account = accountService.getById(account1.getAccountId());
        if (account == null) {
            return Result.fail(Const.ACCOUNT_NOT_EXIST, "账户不存在，无法注销", null);
        }
        // 检查是否已注销
        if (account.getStatu() == 0) {
            return Result.fail(Const.ACCOUNT_STATU_ERR, "该账户已注销，无需重复操作", null);
        }
        // 修改状态为0，更新时间
        account.setStatu(0);
        account.setDelTag(1);
        account.setUpdated(LocalDateTime.now());
        boolean updateSuccess = accountService.updateById(account);
        // 返回结果
        if (updateSuccess) {
            return Result.success("账号注销成功");
        } else {
            return Result.fail("账号注销失败，请重试");
        }
    }

    // 修改密码
    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        log.info("用户{}正在修改密码...", account.getAccountId());
        // 查询账户是否存在
        Account existingAccount = accountService.getById(account.getAccountId());
        if (existingAccount == null) {
            return Result.fail(Const.ACCOUNT_NOT_EXIST, "账户不存在", null);
        }
        // 检查原密码是否正确
        String oldPassword = account.getPassword();
        String md5OldPass = MD5Utils.md5(MD5Utils.inputPassToNewPass(oldPassword));
        if (!md5OldPass.equals(existingAccount.getPassword())) {
            return Result.fail(Const.PASSWORD_ERR, "原密码不正确", null);
        }
        // 获取新密码
        String newPassword = account.getNewPassword();
        String md5NewPass = MD5Utils.md5(MD5Utils.inputPassToNewPass(newPassword));
        // 更新密码
        existingAccount.setPassword(md5NewPass);
        existingAccount.setUpdated(LocalDateTime.now());
        boolean updateSuccess = accountService.updateById(existingAccount);
        if (updateSuccess) {
            return Result.success("密码修改成功");
        } else {
            return Result.fail("密码修改失败，请重试");
        }
    }

    // 修改用户信息（名称、性别）
    @PostMapping("/updateInfo")
    public Result updateInfo(@RequestBody Account account) {
        log.info("用户{}正在修改个人信息...", account.getAccountId());
        // 查询账户是否存在
        Account existingAccount = accountService.getById(account.getAccountId());
        if (existingAccount == null) {
            return Result.fail(Const.ACCOUNT_NOT_EXIST, "账户不存在", null);
        }
        // 更新用户信息
        if (account.getAccountName() != null) {
            existingAccount.setAccountName(account.getAccountName());
        }
        if (account.getAccountSex() != null) {
            existingAccount.setAccountSex(account.getAccountSex());
        }
        existingAccount.setUpdated(LocalDateTime.now());
        boolean updateSuccess = accountService.updateById(existingAccount);
        if (updateSuccess) {
            return Result.success(existingAccount);
        } else {
            return Result.fail("信息修改失败，请重试");
        }
    }

}
