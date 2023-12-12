@Data
public class Mail {
     private String fromName;
     private String fromEmail;
     private List<String> toList; //메일 수신대상
     private List<String> ccMailList; //참조 대상 리스트
     private String ccMails;
     private String title; // 메일 제목
     private String content; // 내용
     private String urlSuffix; //컨텐츠 내부에서 사용될 링크urlSuffix
     private String logMassage; //로그
     private Boolean is_send = false; //전송 성공여부

    @Builder
    public Mail(String fromName, String fromEmail, List<String> toList, List<String> ccMailList, String title, String content, String urlSuffix) {
        String serverType = CmPathInfo.getSERVER_TYPE();

        this.fromName = Optional.ofNullable(fromName).orElse("운영시스템");
        this.fromEmail = Optional.ofNullable(fromEmail).orElse("dbos@aaa.com");
        this.toList = adjustReceiverList(toList, serverType); //수신자 목록 조정(운영, 개발 구분)
        this.ccMailList = serverType.equals("REAL") ? ccMailList : null; //운영서버일 경우 ccMail 세팅
        this.title = title;
        this.content = content;
        //참조리스트가 존재할 경우 할당(운영 + 값이있을때)
        this.ccMails = Optional.ofNullable(ccMailList)
                               .map(list -> String.join(",", list))
                               .orElse(null);
        this.urlSuffix = urlSuffix;
    }

    //서버 타입에 따른 수신자 조정
    private static List<String> adjustReceiverList(List<String> originalList, String serverType) {
        List<String> adjustedList = new ArrayList<>();
        if (serverType.equals("REAL")) {
            adjustedList.addAll(originalList);
            adjustedList.add("mskim@aaa.com"); // 운영계의 모든 메일에 수신인으로 추가
        } else {
            //개발 환경 테스트 수신자 목록
            adjustedList = Arrays.asList(
                "like10495@aaa.com"
            );
        }
        return filterExceptEmails(adjustedList);
    }

    //메일 제외할 수신자 목록
    private static List<String> filterExceptEmails(List<String> mailList) {
        List<String> exceptMailList = Arrays.asList(
              "doc1@aaa.com"
        );
        return mailList.stream()
            .filter(email -> !exceptMailList.contains(email))
            .collect(Collectors.toList());
    }
}
