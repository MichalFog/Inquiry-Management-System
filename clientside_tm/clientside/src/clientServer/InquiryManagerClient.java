package clientServer;

import Data.Complaint;
import Data.Inquiry;
import Data.Question;
import Data.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Month;
import java.util.*;

public class InquiryManagerClient {
    private Inquiry tempInquiry;
    private Socket connectToServer;
    private RequestData requestData;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    ResponseData responseData;
    int inquiryCode;
    public InquiryManagerClient(String address, int port) {
        try {
            connectToServer = new Socket(address, port);
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void execut() {
        try (Scanner scanner = new Scanner(System.in)) {
            int choiceNumber;

            do {
                System.out.println("Enter 1 to get all inquiries \n 2 to add inquiry \n 3 to get inquiry status \n 4 to check representative details\n 5 to get all the inquirys in specific month \n 6 to get all inquiries by representative \n 7  to cancel inquiry  \n 8 for exit");
                System.out.print("Enter your choice: ");
                choiceNumber = scanner.nextInt();
                switch (choiceNumber) {
                    case 1:
                        requestData = new RequestData(InquiryManagerActions.ALL_INQUIRY);
                        sendRequest(requestData);
                        break;
                    case 2:
                        System.out.println("Enter inquiry type: 1 = Question, 2 = Request, 3 = Complaint");
                        int inquiryType = scanner.nextInt();
                        tempInquiry = createInquiry(inquiryType);
                        if (tempInquiry != null) {
                            requestData = new RequestData(InquiryManagerActions.ADD_INQUIRY,
                                    Collections.singletonList(tempInquiry));
                            sendRequest(requestData);
                        } else {
                            System.out.println("Invalid inquiry type. Please try again.");
                        }
                        break;
                    case 3:
                        System.out.println("Enter inquiry  code");
                        inquiryCode = scanner.nextInt();
                        requestData = new RequestData(InquiryManagerActions.GET_STATUS,Collections.singletonList(inquiryCode));
                        sendRequest(requestData);
                        break;
                    case 4:
                        System.out.println("Enter inquiry code");
                        inquiryCode = scanner.nextInt();
                        requestData = new RequestData(InquiryManagerActions.REP_BY_INQUIRY,Collections.singletonList(inquiryCode));
                        sendRequest(requestData);
                        break;
                    case 5:
                        System.out.println("Enter month ");
                        int intMonth = scanner.nextInt();
                        Month month =Month.of(intMonth);
                        requestData = new RequestData(InquiryManagerActions.INQUIRY_IN_MONTH,Collections.singletonList(month));
                        sendRequest(requestData);
                        break;
                    case 6:
                         System.out.println("Enter name of Representative");
                         String nameOfRepresentative = scanner.nextLine();
                         while (nameOfRepresentative=="")
                             nameOfRepresentative = scanner.nextLine();
                         requestData = new RequestData(InquiryManagerActions.ALL_INQUIRY_BY_REP,Collections.singletonList(nameOfRepresentative));
                         sendRequest(requestData);
                         break;
                    case 7:
                        System.out.println("Enter inquiry code");
                        inquiryCode = scanner.nextInt();
                        requestData =new RequestData(InquiryManagerActions.CANCEL_INQUIRY,Collections.singletonList(inquiryCode));
                        sendRequest(requestData);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }

            } while (choiceNumber < 0 && choiceNumber < 8);

        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public Inquiry createInquiry(int code) {
        switch (code) {
            case 1:
                return new Question().fillDataByUser();
            case 2:
                return new Request().fillDataByUser();
            case 3:
                return new Complaint().fillDataByUser();
            default:
                return null;
        }
    }

    public void sendRequest(RequestData requestData) {
        try {
            objectOutputStream = new ObjectOutputStream(connectToServer.getOutputStream());
            objectOutputStream.writeObject(requestData);
            objectOutputStream.flush();

            objectInputStream = new ObjectInputStream(connectToServer.getInputStream());
             responseData = (ResponseData) objectInputStream.readObject();
            System.out.println("Status: " + responseData.getStatus());
            System.out.println("Message: " + responseData.getMessage());
            System.out.println("Result: " + responseData.getResult());
//            System.out.println(responseData.getResult());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error during communication with server: " + e.getMessage());
        }
    }
}
