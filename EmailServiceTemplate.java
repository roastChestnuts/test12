package com.amore.apbos.service.email;

import com.amore.apbos.model.Mail;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author 안병찬
 * @createDate 2023-12-11
 * @description : 이메일 템플릿 콜백 패턴
 * */
public class EmailServiceTemplate {
    public void sendEmail(Supplier<Map<String, Object>> dataMapper,
                          Supplier<Mail> mailCreator,
                          BiConsumer<Mail, Map<String, Object>> mailSender) {
        //데이터 조회
        Map<String, Object> data = dataMapper.get();
        //메일객체 조회
        Mail mail = mailCreator.get();
        //메일전송
        mailSender.accept(mail, data);
    }
}
