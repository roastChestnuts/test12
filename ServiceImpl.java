    @Override
    public CmMap<String, Object> invoicePopupWithdraw(int invoice_seq, String memId) {
        CmMap<String, Object> resMap = new CmMap<>();
        resMap.put("status", "fail");
        int cnt = 0;
        CmMap<String, Object> paramMap = new CmMap<>();
        try {
            paramMap.put("invoice_seq", invoice_seq);
            paramMap.put("memId", memId);
            paramMap.put("isSaveCorpInfo", false);
            CmMap<String, Object> cmMap = invoiceDao.getInvoicePopup(paramMap);
            switch (cmMap.getString("status")) {
                case "c1":
                    paramMap.put("status", "y");
                    cnt = invoiceDao.updateInvoiceData(paramMap);// 1.status update
                    if(cnt > 0){
                        invoiceDao.deleteInvoicePaymentData(paramMap); // 2.PaymentData delete
                    }
                    //템플릿에 매핑할 데이터
                    Map<String, Object> data = Optional.ofNullable(invoiceDao.getInvoiceReportData(invoice_seq)).orElse(new HashMap<>());
                    //메일 객체 생성
                    Mail mail = Mail.builder()
                                    .title("Invoice 수취확인 취소 안내 메일")
                                    .toList(CmMail.getMangerMails()) //수신자리스트
                                    .urlSuffix("invoice/invoice_popup.do") //메일에서 링크로 사용될 urlSuffix
                                    .build();
                    emailService.sendMail(mail, "invoice/receipt_cancel", data);
                    break;
                case "c2":
                    paramMap.put("status", "c1");
                    cnt = invoiceDao.updateInvoiceData(paramMap);// status update
                    break;
            }
            if (cnt > 0) {
                resMap.put("status", "success");
                resMap.put("message", "Successfully Withdrawn");
            }
            if (resMap.get("status").equals("fail")) {
                logger.error("invoicePopupWithdraw ::: status is fail invoice_seq : {}, memId : {}", invoice_seq, memId);
                throw new Exception();
            }
        }catch (Exception e){
            logger.error("exception", e);
        }
        return resMap;
    }
