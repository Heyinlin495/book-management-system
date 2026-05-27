package com.example._025021238heyinlin.controller;

import com.example._025021238heyinlin.dto.AdminAddUserRequest;
import com.example._025021238heyinlin.dto.ApiResponse;
import com.example._025021238heyinlin.dto.LoginRequest;
import com.example._025021238heyinlin.dto.LoginResponse;
import com.example._025021238heyinlin.dto.UpdateAvatarRequest;
import com.example._025021238heyinlin.dto.UpdateProfileRequest;
import com.example._025021238heyinlin.dto.UserDTO;
import com.example._025021238heyinlin.jwt.JwtUtils;
import com.example._025021238heyinlin.jwt.UserDetailsImpl;
import com.example._025021238heyinlin.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        log.info("获取所有用户");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        log.info("获取用户，ID: {}", id);
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(ApiResponse.success(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "用户未找到")));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        log.info("用户登录请求: {}", request.getUsername());

        if (!CaptchaController.validateCaptcha(request.getCaptchaKey(), request.getCaptchaCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "验证码错误或已过期"));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDTO user = userService.getUserByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            if ("ADMIN".equals(user.getRole())) {
                log.warn("管理员 {} 尝试从用户端登录", request.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "管理员请从管理后台登录"));
            }

            String token = jwtUtils.generateJwtToken(authentication);
            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .user(user)
                    .build();

            log.info("用户 {} 登录成功", request.getUsername());
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (BadCredentialsException e) {
            log.warn("用户 {} 登录失败", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "用户名或密码错误"));
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<ApiResponse<LoginResponse>> adminLogin(@RequestBody LoginRequest request) {
        log.info("管理员登录请求: {}", request.getUsername());

        if (!CaptchaController.validateCaptcha(request.getCaptchaKey(), request.getCaptchaCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "验证码错误或已过期"));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
            UserDTO user = userService.getUserByUsername(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            if (!"ADMIN".equals(user.getRole())) {
                log.warn("普通用户 {} 尝试从管理后台登录", request.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "非管理员无法登录管理后台"));
            }

            String token = jwtUtils.generateJwtToken(authentication);
            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .user(user)
                    .build();

            log.info("管理员 {} 登录成功", request.getUsername());
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (BadCredentialsException e) {
            log.warn("管理员 {} 登录失败", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "用户名或密码错误"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@RequestBody LoginRequest request,
                                                         @RequestParam(required = false) String email) {
        log.info("用户注册: {}", request.getUsername());
        try {
            UserDTO user = userService.createUser(request.getUsername(), request.getPassword(), 
                                                 email != null ? email : "", "USER");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("注册成功", user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Long id,
                                                           @RequestParam(required = false) String email,
                                                           @RequestParam(required = false) String role) {
        log.info("更新用户，ID: {}", id);
        try {
            UserDTO updatedUser = userService.updateUser(id, email, role);
            return ResponseEntity.ok(ApiResponse.success("用户更新成功", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("删除用户，ID: {}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("用户删除成功", null));
        } catch (RuntimeException e) {
            log.error("删除用户失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable Long id,
                                                            @RequestParam String newPassword,
                                                            Authentication authentication) {
        log.info("修改用户密码，ID: {}", id);
        try {
            ensureSelfOrAdmin(id, authentication);
            userService.changePassword(id, newPassword);
            return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @PostMapping("/admin/add")
    public ResponseEntity<ApiResponse<UserDTO>> adminAddUser(@RequestBody AdminAddUserRequest request) {
        log.info("管理员添加用户: {}", request.getUsername());
        try {
            UserDTO user = userService.createUser(request.getUsername(), request.getPassword(),
                    request.getEmail(), request.getRole() != null ? request.getRole() : "USER");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("用户添加成功", user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(@PathVariable Long id,
                                                               @RequestBody UpdateProfileRequest request,
                                                               Authentication authentication) {
        log.info("更新个人信息，用户ID: {}", id);
        try {
            ensureSelfOrAdmin(id, authentication);
            UserDTO updatedUser = userService.updateProfile(id, request);
            return ResponseEntity.ok(ApiResponse.success("个人信息更新成功", updatedUser));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        } catch (RuntimeException e) {
            log.error("更新个人信息失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/{id}/avatar")
    public ResponseEntity<ApiResponse<UserDTO>> updateAvatar(@PathVariable Long id,
                                                              @RequestBody UpdateAvatarRequest request,
                                                              Authentication authentication) {
        log.info("更新用户头像，用户ID: {}", id);
        try {
            ensureSelfOrAdmin(id, authentication);
            UserDTO updatedUser = userService.updateAvatar(id, request.getAvatar());
            return ResponseEntity.ok(ApiResponse.success("头像更新成功", updatedUser));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        } catch (RuntimeException e) {
            log.error("更新头像失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    private void ensureSelfOrAdmin(Long targetUserId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl principal)) {
            throw new IllegalStateException("未登录用户无法执行该操作");
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        if (!isAdmin && !targetUserId.equals(principal.getId())) {
            throw new IllegalStateException("无权操作其他用户的信息");
        }
    }
}
