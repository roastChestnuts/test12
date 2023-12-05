@FunctionalInterface
public interface DataMapper {
    Map<String, Object> mapData();
}

@FunctionalInterface
public interface MailCreator {
    Mail createMail();
}

@FunctionalInterface
public interface MailSender {
    void sendMail(Mail mail, Map<String, Object> data);
}

public class EmailServiceTemplate {

    public void sendEmail(DataMapper dataMapper, MailCreator mailCreator, MailSender mailSender) {
        Map<String, Object> data = dataMapper.mapData();
        Mail mail = mailCreator.createMail();
        mailSender.sendMail(mail, data);
    }
}

public class EmailClient {

    private EmailServiceTemplate emailServiceTemplate = new EmailServiceTemplate();
    private InvoiceDao invoiceDao;
    private EmailService emailService;

    public void sendInvoiceCancellationEmail(Long invoiceSeq) {
        emailServiceTemplate.sendEmail(
            // 데이터 매핑
            () -> Optional.ofNullable(invoiceDao.getInvoiceReportData(invoiceSeq)).orElse(new HashMap<>()),
            
            // 메일 객체 생성
            () -> Mail.builder()
                      .title("Invoice 수취확인 취소 안내 메일")
                      .toList(CmMail.getMangerMails())
                      .urlSuffix("invoice/invoice_popup.do")
                      .build(),
            
            // 메일 전송
            (mail, data) -> emailService.sendMail(mail, "invoice/receipt_cancel", data)
        );
    }
}
