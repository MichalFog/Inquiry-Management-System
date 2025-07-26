package clientServer;

import Business.InquiryHandling;
import Business.InquiryManager;
import Data.Inquiry;
import Data.InquiryException;
import Data.Representative;
import HandleStoreFiles.HandleFiles;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.time.Month;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Business.InquiryManager.*;

public class HandleClient extends Thread {
    private Socket clientSocket;
    ResponseData response = null;
    Inquiry inquiry = null;
    InquiryManager inquiryManager = InquiryManager.getInstance();

    public HandleClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        startServer();
    }

    public synchronized void startServer() {
        try {
            handleClientRequest();
        } catch (IOException | ClassNotFoundException | InquiryException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleClientRequest() throws IOException, ClassNotFoundException, InterruptedException, InquiryException {
        ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        RequestData requestData = (RequestData) objectInputStream.readObject();
        System.out.println("requestData : " + requestData.parameters);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

        switch (requestData.action) {
            case ALL_INQUIRY:
                response = new ResponseData(ResponseStatus.SUCCESS, "Your request was received successfully. Here are all the inquiries.", allInquiries);
                break;

            case ADD_INQUIRY:
                if (requestData.parameters != null && !requestData.parameters.isEmpty()) {
                    Object param = requestData.parameters.get(0);
                    if (param instanceof Inquiry) {
                        List<Object> mutableParameters = new ArrayList<>(requestData.parameters);
                        inquiry = inquiryManager.registerInquiry(mutableParameters);
                        response = new ResponseData(ResponseStatus.SUCCESS, "The inquiry was added successfully", inquiry);
                    } else {
                        response = new ResponseData(ResponseStatus.FAIL, "Error: The inquiry is not valid");
                    }
                } else {
                    response = new ResponseData(ResponseStatus.FAIL, "Error: No parameters were sent");
                }
                break;

            case GET_STATUS:
                String statusInquiry = null;
                if (requestData.parameters != null && !requestData.parameters.isEmpty()) {
                    Object params = requestData.parameters.get(0);
                    statusInquiry = InquiryManager.getStatusInquiry((Integer) params);
                    response = new ResponseData(ResponseStatus.SUCCESS, "Inquiry status", statusInquiry);
                } else response = new ResponseData(ResponseStatus.FAIL, "Failed to retrieve inquiry status");
                break;

            case REP_BY_INQUIRY:
                if (requestData.parameters != null && !requestData.parameters.isEmpty()) {
                    Object params = requestData.parameters.get(0);
                    String name = InquiryManager.getNameRepresentativeByInquiry((Integer) params);
                    response = new ResponseData(ResponseStatus.SUCCESS, "Representative retrieved by inquiry", name);
                } else {
                    response = new ResponseData(ResponseStatus.FAIL, "Failed to retrieve representative by inquiry", null);
                }
                break;

            case INQUIRY_IN_MONTH:
                if (requestData.parameters != null && !requestData.parameters.isEmpty()) {
                    try {
                        String monthStr = requestData.parameters.get(0).toString().toUpperCase();
                        Month month = Month.valueOf(monthStr);
                        int amount = InquiryManager.getAmountOfInquiryInMonth(month);
                        response = new ResponseData(ResponseStatus.SUCCESS, "Inquiries for the month of " + month + " were successfully returned", amount);
                    } catch (IllegalArgumentException e) {
                        response = new ResponseData(ResponseStatus.FAIL, "Invalid month received in the request");
                    }
                } else {
                    response = new ResponseData(ResponseStatus.FAIL, "No month was received in the request");
                }
                break;

            case ALL_INQUIRY_BY_REP:
                if (requestData.parameters != null && !requestData.parameters.isEmpty()) {
                    String repName = requestData.parameters.get(0).toString();
                    List<Inquiry> inquiriesByRep = InquiryManager.getInquiriesByRepresentative(repName);
                    if (inquiriesByRep.isEmpty())
                        response = new ResponseData(ResponseStatus.FAIL, "Error: No representative name was sent");
                    else
                        response = new ResponseData(ResponseStatus.SUCCESS, "Representative's inquiries were successfully returned", inquiriesByRep);
                } else {
                    response = new ResponseData(ResponseStatus.FAIL, "Error: No representative name was sent");
                }
                break;

            case CANCEL_INQUIRY:
                try {
                    inquiryManager.cancelInquiry((Integer) requestData.parameters.get(0));
                    response = new ResponseData(ResponseStatus.SUCCESS, "The inquiry was successfully canceled", requestData.parameters.get(0));
                } catch (IllegalArgumentException e) {
                    response = new ResponseData(ResponseStatus.FAIL, e.getMessage(), null);
                }
                break;

            default:
                response = new ResponseData(ResponseStatus.FAIL, "Error: Unknown action");
        }

        objectOutputStream.writeObject(response);
        objectOutputStream.flush();
    }
}
