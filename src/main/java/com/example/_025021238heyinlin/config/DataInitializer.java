package com.example._025021238heyinlin.config;

import com.example._025021238heyinlin.entity.ReadingRoom;
import com.example._025021238heyinlin.entity.Seat;
import com.example._025021238heyinlin.entity.User;
import com.example._025021238heyinlin.repository.ReadingRoomRepository;
import com.example._025021238heyinlin.repository.SeatRepository;
import com.example._025021238heyinlin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 数据初始化器
 * 应用启动时自动检查并创建默认管理员账户
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReadingRoomRepository readingRoomRepository;
    private final SeatRepository seatRepository;

    // 默认管理员账户配置
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "123456";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@example.com";

    // 何胤霖管理员账户配置
    private static final String HYL_ADMIN_USERNAME = "何胤霖";
    private static final String HYL_ADMIN_PASSWORD = "123456";
    private static final String HYL_ADMIN_EMAIL = "heyinlin@example.com";

    @Override
    public void run(String... args) {
        initDefaultAdmin();
        initHylAdmin();
        validateExistingPasswords();
        initReadingRoomSeats();
    }

    /**
     * 初始化默认管理员账户
     */
    private void initDefaultAdmin() {
        Optional<User> existingAdmin = userRepository.findByUsername(DEFAULT_ADMIN_USERNAME);
        if (existingAdmin.isEmpty()) {
            User admin = User.builder()
                    .username(DEFAULT_ADMIN_USERNAME)
                    .password(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD))
                    .email(DEFAULT_ADMIN_EMAIL)
                    .role("ADMIN")
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            log.info("========================================");
            log.info("默认管理员账户已创建:");
            log.info("用户名: {}", DEFAULT_ADMIN_USERNAME);
            log.info("密码: {}", DEFAULT_ADMIN_PASSWORD);
            log.info("========================================");
        } else {
            User admin = existingAdmin.get();
            if (!passwordEncoder.matches(DEFAULT_ADMIN_PASSWORD, admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
                userRepository.save(admin);
                log.info("管理员账户 '{}' 密码已同步为默认值", DEFAULT_ADMIN_USERNAME);
            } else {
                log.info("管理员账户 '{}' 已存在", DEFAULT_ADMIN_USERNAME);
            }
        }
    }

    /**
     * 初始化何胤霖管理员账户
     */
    private void initHylAdmin() {
        Optional<User> existingAdmin = userRepository.findByUsername(HYL_ADMIN_USERNAME);
        if (existingAdmin.isEmpty()) {
            User admin = User.builder()
                    .username(HYL_ADMIN_USERNAME)
                    .password(passwordEncoder.encode(HYL_ADMIN_PASSWORD))
                    .email(HYL_ADMIN_EMAIL)
                    .role("ADMIN")
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            log.info("========================================");
            log.info("管理员账户已创建:");
            log.info("用户名: {}", HYL_ADMIN_USERNAME);
            log.info("密码: {}", HYL_ADMIN_PASSWORD);
            log.info("========================================");
        } else {
            User admin = existingAdmin.get();
            if (!passwordEncoder.matches(HYL_ADMIN_PASSWORD, admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode(HYL_ADMIN_PASSWORD));
                userRepository.save(admin);
                log.info("管理员账户 '{}' 密码已同步为默认值", HYL_ADMIN_USERNAME);
            } else {
                log.info("管理员账户 '{}' 已存在", HYL_ADMIN_USERNAME);
            }
        }
    }

    /**
     * 验证现有用户密码格式
     * 检查数据库中是否有无效的BCrypt密码格式
     */
    private void validateExistingPasswords() {
        userRepository.findAll().forEach(user -> {
            String password = user.getPassword();
            if (password == null || !isValidBCryptFormat(password)) {
                log.warn("用户 '{}' (ID: {}) 的密码格式无效，正在重置为默认密码...", 
                        user.getUsername(), user.getId());
                user.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
                userRepository.save(user);
                log.info("用户 '{}' 密码已重置为: {}", user.getUsername(), DEFAULT_ADMIN_PASSWORD);
            }
        });
    }

    /**
     * 检查密码是否为有效的BCrypt格式
     */
    private boolean isValidBCryptFormat(String password) {
        // BCrypt格式: $2a$、$2b$ 或 $2y$ 开头，总长度为60字符
        return password != null 
                && password.length() == 60 
                && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    /**
     * 初始化阅览室座位
     * 检查每个阅览室是否有座位，如果没有则自动创建
     */
    private void initReadingRoomSeats() {
        List<ReadingRoom> rooms = readingRoomRepository.findAll();
        for (ReadingRoom room : rooms) {
            List<Seat> existingSeats = seatRepository.findByRoomIdAndIsActiveTrueOrderBySeatNumberAsc(room.getId());
            if (existingSeats.isEmpty() && room.getTotalSeats() != null && room.getTotalSeats() > 0) {
                log.info("为阅览室 '{}' 创建 {} 个座位...", room.getName(), room.getTotalSeats());
                for (int i = 1; i <= room.getTotalSeats(); i++) {
                    Seat seat = Seat.builder()
                            .room(room)
                            .seatNumber(String.format("%03d", i))
                            .rowNumber((i - 1) / 10 + 1)
                            .columnNumber((i - 1) % 10 + 1)
                            .hasPower(i % 5 == 0) // 每5个座位有一个带电源
                            .nearWindow(i <= 10) // 前10个座位靠窗
                            .status("AVAILABLE")
                            .isActive(true)
                            .build();
                    seatRepository.save(seat);
                }
                log.info("阅览室 '{}' 座位创建完成", room.getName());
            }
        }
    }
}
