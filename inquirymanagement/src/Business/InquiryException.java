package Business;


    public class InquiryException extends Exception {
        private int inquiryCode;

        public InquiryException( String message ,int inquiryCode) {
            super(message);
            this.inquiryCode = inquiryCode;
        }
        public InquiryException(String message) {
            super(message);
        }




        @Override
        public String getMessage() {
            return "Inquiry Code: " + inquiryCode + ", Error: " + super.getMessage();
        }
    }




