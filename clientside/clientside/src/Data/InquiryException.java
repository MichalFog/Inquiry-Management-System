package Data;

public class InquiryException extends Exception {
    private int inquiryCode;

    public InquiryException(int inquiryCode, String message) {
        super(message);
        this.inquiryCode = inquiryCode;
    }

    @Override
    public String getMessage() {
        return "Inquiry Code: " + inquiryCode + ", Error: " + super.getMessage();
    }
}

