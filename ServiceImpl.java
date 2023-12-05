    @Override
    public Map<String, Object> invoicePopupWithdraw(int invoice_seq, String memId) {
        try {
                ...
                //템플릿에 매핑할 데이터
                Map<String, Object> data = Optional.ofNullable(invoiceDao.getInvoiceReportData(invoice_seq)).orElse(new HashMap<>());
                //메일 객체 생성
                Mail mail = Mail.builder()
                                .title("Invoice 수취확인 취소 안내 메일")
                                .toList(CmMail.getMangerMails()) //수신자리스트
                                .urlSuffix("invoice/invoice_popup.do") //메일에서 링크로 사용될 urlSuffix
                                .build();
                emailService.sendMail(mail, "invoice/receipt_cancel", data);
            ...
        }catch (Exception e){
            logger.error("exception", e);
        }
        return resMap;
    }
