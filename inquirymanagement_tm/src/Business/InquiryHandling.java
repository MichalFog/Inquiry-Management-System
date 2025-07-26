package Business;

import Data.Complaint;
import Data.Inquiry;
import Data.Question;
import Data.Request;

import java.util.Scanner;

public class InquiryHandling extends Thread {
    private Inquiry currentInquiry;

    public Inquiry getCurrentInquiry() {
        return currentInquiry;
    }

    public void setCurrentInquiry(Inquiry currentInquiry) {
        this.currentInquiry = currentInquiry;
    }

    @Deprecated
    public void createInquiry() {
        System.out.println("enter 1 for Question 2 for Request 3 for Complaint");
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        switch (number) {
            case 1:
                currentInquiry = new Question();
                break;
            case 2:
                currentInquiry = new Request();
                break;
            case 3:
                currentInquiry = new Complaint();
                break;
            default:
                System.out.println("the isn't such a quiry type ");
                break;
        }
        currentInquiry.fillDataByUser();
    }

    @Override
    public void run() {
        try {
            currentInquiry.handling();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
