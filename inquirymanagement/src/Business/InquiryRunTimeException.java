package Business;

public class InquiryRunTimeException extends RuntimeException {
    private int inquiryCode;

    public InquiryRunTimeException(int inquiryCode, String message) {
        super(message);
        this.inquiryCode = inquiryCode;
    }

    @Override
    public String getMessage() {
        return "Inquiry Code: " + inquiryCode + ", Runtime Error: " + super.getMessage();
    }
}