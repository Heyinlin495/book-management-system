package com.example._025021238heyinlin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/captcha")
@Slf4j
public class CaptchaController {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final long EXPIRE_MINUTES = 5;

    private static final ConcurrentHashMap<String, CaptchaEntry> captchaStore = new ConcurrentHashMap<>();
    private static final Random random = new Random();
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";

    static {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "captcha-cleaner");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            captchaStore.entrySet().removeIf(entry -> now - entry.getValue().createTime > EXPIRE_MINUTES * 60 * 1000);
        }, 1, 1, TimeUnit.MINUTES);
    }

    @GetMapping("/generate")
    public ResponseEntity<Map<String, String>> generateCaptcha() {
        String key = java.util.UUID.randomUUID().toString().replace("-", "");
        String code = generateCode();

        captchaStore.put(key, new CaptchaEntry(code.toLowerCase(), System.currentTimeMillis()));

        String imageBase64 = generateCaptchaImage(code);

        log.info("验证码已生成, key: {}", key);
        return ResponseEntity.ok(Map.of(
                "key", key,
                "image", "data:image/png;base64," + imageBase64
        ));
    }

    public static boolean validateCaptcha(String key, String code) {
        if (key == null || code == null) {
            return false;
        }
        CaptchaEntry entry = captchaStore.remove(key);
        if (entry == null) {
            return false;
        }
        if (System.currentTimeMillis() - entry.createTime > EXPIRE_MINUTES * 60 * 1000) {
            return false;
        }
        return entry.code.equalsIgnoreCase(code.trim());
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private String generateCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        for (int i = 0; i < 5; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.setColor(getRandomColor(160));
            g.drawLine(x1, y1, x2, y2);
        }

        for (int i = 0; i < 30; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.setColor(getRandomColor(200));
            g.fillOval(x, y, 2, 2);
        }

        int fontSize = HEIGHT - 10;
        g.setFont(new Font("SansSerif", Font.BOLD, fontSize));

        for (int i = 0; i < code.length(); i++) {
            g.setColor(getRandomColor(100));
            int x = i * (WIDTH / CODE_LENGTH) + 5;
            int y = fontSize + (random.nextInt(6) - 3);
            double theta = Math.toRadians(random.nextInt(30) - 15);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.rotate(theta, x + fontSize / 4.0, y - fontSize / 4.0);
            g2.drawString(String.valueOf(code.charAt(i)), x, y);
            g2.dispose();
        }

        g.dispose();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            log.error("生成验证码图片失败", e);
            return "";
        }
    }

    private Color getRandomColor(int max) {
        return new Color(
                random.nextInt(max),
                random.nextInt(max),
                random.nextInt(max)
        );
    }

    private static class CaptchaEntry {
        final String code;
        final long createTime;

        CaptchaEntry(String code, long createTime) {
            this.code = code;
            this.createTime = createTime;
        }
    }
}
