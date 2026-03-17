package com.seodong.portfolio.contact;

import com.seodong.portfolio.common.dto.MessageResponse;
import com.seodong.portfolio.common.exception.RateLimitException;
import com.seodong.portfolio.contact.dto.ContactRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${rate-limit.contact.capacity:3}")
    private int rateLimitCapacity;

    @Value("${rate-limit.contact.refill-minutes:1}")
    private long rateLimitRefillMinutes;

    @Transactional
    public MessageResponse sendContact(ContactRequest req, String clientIp) {
        checkRateLimit(clientIp);

        contactRepository.save(Contact.builder()
                .name(req.name())
                .email(req.email())
                .message(req.message())
                .build());

        sendEmail(req);

        return new MessageResponse(true, "문의가 정상적으로 전송되었습니다.");
    }

    private void checkRateLimit(String clientIp) {
        String key = "rl:contact:" + clientIp;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, rateLimitRefillMinutes * 60, TimeUnit.SECONDS);
        }
        if (count != null && count > rateLimitCapacity) {
            throw new RateLimitException("요청 한도를 초과했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    private void sendEmail(ContactRequest req) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(fromEmail);
        mail.setTo(fromEmail);
        mail.setSubject("[포트폴리오 문의] " + req.name());
        mail.setText(
                "이름: " + req.name() + "\n" +
                "이메일: " + req.email() + "\n\n" +
                "내용:\n" + req.message()
        );
        mailSender.send(mail);
    }
}
