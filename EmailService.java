@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private SpringTemplateEngine templateEngine;

    //템플릿 + 데이터 렌더링
    public String renderTemplate(String templateName, Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process(templateName, context);
    }

    /**
     * @param mail : 메일 객체
     * @param templateName : 보낼 메일 템플릿명
     * @param data : 템플릿에 렌더링 할 데이터
     * */
    @Async("mailExecutor")
    public void sendMail(Mail mail, String templateName, Map<String, Object> data) {
        try {
            // url데이터 세팅
            data.put("url", CmPathInfo.getWEB_FULL_URL() + mail.getUrlSuffix());

            // 템플릿 렌더링
            String content = renderTemplate(templateName, data);
            mail.setContent(content);

            // 메일 전송
            CmMail.sendMailByTemplate(mail);
        } catch (TaskRejectedException e) {
            // 스레드 풀 용량 초과로 작업을 수용할 수 없을 때의 예외 처리
            logger.error("TaskRejectedException: 스레드 풀 용량 초과", e);
        } catch (Exception e) {
            logger.error("EmailService.Exception: 메일 전송 중 예상치 못한 오류 발생", e);
        }
    }
}
