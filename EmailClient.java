@Service
public class EmailClient {
    private EmailServiceTemplate emailServiceTemplate = new EmailServiceTemplate();
    @Autowired
    private InvoiceDao invoiceDao;
    @Autowired
    private EmailService emailService;

    /**
     * @param title 메일제목
     * @param invoiceSeq 메일 데이터 조회 seq
     * @param templateName 메일 보낼 템플릿 명
     * @apiNote 인보이스 팝업 저장 메일 발송
     * */
    public void sendInvoicePopupSaveEmail(int invoiceSeq, String title, String templateName) {
        //데이터 조회가 복잡해진다면 데이터 조회 로직에 파사드패턴 적용 고려
        emailServiceTemplate.sendEmail(
            //메일에 바인딩할 데이터 조회
            () -> Optional.ofNullable(invoiceDao.getInvoiceReportData(invoiceSeq)).orElse(new HashMap<>()),
            //메일 기본값 세팅(제목, 수신자, 링크url)
            () -> Mail.builder()
                      .title(title)
                      .toList(CmMail.getMangerMails())
                      .urlSuffix("invoice/invoice_popup.do")
                      .build(),
            //메일 전송
            (mail, data) -> emailService.sendMail(mail, templateName, data)
        );
    }

    //인보이스 독촉메일
    public void sendInvoiceRequestMail(int invoiceSeq, String templateName) {
        Map<String, Object> mailInfo = new HashMap<>(invoiceDao.getReportMailInfo(invoiceSeq)); //현재 선택한 항목에 입력할 메일정보
        List<String> sendMailTargetList = invoiceDao.sendMailTargetList(mailInfo.get("corp_cd").toString()); //해당 인보이스 대상 법인에 등록된 담당자 정보 조회
        List<String> ccMailList = CmMail.getMangerMails(); //참조자
        //yyyyMM -> yyyy/MM
        mailInfo.computeIfPresent("cb_start_ym", (k, v) -> v.toString().substring(0, 4) + "/" + v.toString().substring(4, 6));
        mailInfo.computeIfPresent("cb_end_ym", (k, v) -> v.toString().substring(0, 4) + "/" + v.toString().substring(4, 6));

        String title = "IT Chargeback Invoice have been issued. ("+mailInfo.get("cb_start_ym")+mailInfo.get("cb_end_ym")+")";

        emailServiceTemplate.sendEmail(
            () -> Optional.ofNullable(mailInfo).orElse(new HashMap<>()),
            //메일 기본값 세팅(제목, 수신자, 링크url)
            () -> Mail.builder()
                      .fromName("AP Digital Budget Operating System")
                      .title(title)
                      .toList(sendMailTargetList)
                      .ccMailList(ccMailList)
                      .urlSuffix("invoice/list.do")
                      .build(),
            //메일 전송
            (mail, data) -> emailService.sendMail(mail, templateName, data)
        );
    }
}
