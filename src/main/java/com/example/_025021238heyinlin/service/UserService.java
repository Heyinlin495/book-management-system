package com.example._025021238heyinlin.service;

import com.example._025021238heyinlin.dto.UpdateProfileRequest;
import com.example._025021238heyinlin.dto.UserDTO;
import com.example._025021238heyinlin.entity.User;
import com.example._025021238heyinlin.repository.UserRepository;
import com.example._025021238heyinlin.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BorrowRecordRepository borrowRecordRepository;

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.info("获取所有用户");
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        log.info("获取用户，ID: {}", id);
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByUsername(String username) {
        log.info("获取用户，用户名: {}", username);
        return userRepository.findByUsername(username)
                .map(this::convertToDTO);
    }

    @Transactional
    public UserDTO createUser(String username, String password, String email, String role) {
        log.info("创建新用户: {}", username);
        
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        // 只有邮箱不为空时才检查是否存在
        if (email != null && !email.trim().isEmpty() && userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在");
        }
        
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email != null ? email : "")
                .role(role != null ? role : "USER")
                .build();
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(Long id, String email, String role) {
        log.info("更新用户，ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户未找到"));

        if (email != null) {
            String normalizedEmail = email.trim();
            if (normalizedEmail.isEmpty()) {
                throw new RuntimeException("邮箱不能为空");
            }
            if (userRepository.existsByEmail(normalizedEmail) && !normalizedEmail.equals(user.getEmail())) {
                throw new RuntimeException("邮箱已存在");
            }
            user.setEmail(normalizedEmail);
        }
        if (role != null) {
            user.setRole(role);
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("删除用户，ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 先删除用户的所有借阅记录
        borrowRecordRepository.deleteByUserId(id);
        log.info("已删除用户 {} 的所有借阅记录", id);
        
        // 再删除用户
        userRepository.deleteById(id);
        log.info("用户删除成功，ID: {}", id);
    }

    @Transactional(readOnly = true)
    public boolean validatePassword(String username, String password) {
        log.debug("验证用户密码: {}", username);
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            log.debug("用户 {} 不存在", username);
            return false;
        }
        
        User user = userOpt.get();
        String storedPassword = user.getPassword();
        
        // 检查密码格式是否有效
        if (storedPassword == null || !isValidBCryptFormat(storedPassword)) {
            log.warn("用户 {} 的密码格式无效", username);
            return false;
        }
        
        return passwordEncoder.matches(password, storedPassword);
    }
    
    /**
     * 检查密码是否为有效的BCrypt格式
     */
    private boolean isValidBCryptFormat(String password) {
        return password != null 
                && password.length() == 60 
                && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    @Transactional
    public void changePassword(Long id, String newPassword) {
        log.info("修改用户密码，ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .avatar(user.getAvatar())
                .build();
    }

    @Transactional
    public UserDTO updateProfile(Long id, UpdateProfileRequest request) {
        log.info("更新个人信息，ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        
        // 更新邮箱
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // 检查邮箱是否已被其他用户使用
            if (userRepository.existsByEmail(request.getEmail()) && 
                !request.getEmail().equals(user.getEmail())) {
                throw new RuntimeException("该邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }
        
        // 修改密码（需要验证当前密码）
        if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
                throw new RuntimeException("请输入当前密码");
            }
            
            // 验证当前密码
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("当前密码错误");
            }
            
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Transactional
    public UserDTO updateAvatar(Long id, String avatar) {
        log.info("更新用户头像，ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        
        user.setAvatar(avatar);
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
}
