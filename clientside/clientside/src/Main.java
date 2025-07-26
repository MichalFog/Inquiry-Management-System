import clientServer.InquiryManagerClient;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        InquiryManagerClient client = new InquiryManagerClient("localhost", 12345);
        client.execut();
    }
}
