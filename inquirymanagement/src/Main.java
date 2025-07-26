import Business.InquiryManager;
import Business.RepresentativeManager;
import clientServer.InquiryManagerServer;
import process.RanameFile;
import processNightly.NightlyFileDeletion;

import java.io.IOException;
import java.net.ServerSocket;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {

//        NightlyFileDeletion nightlyFileDeletion = new NightlyFileDeletion("C:\\Users\\User\\Documents\\תיכנות\\git\\inquirymanagement_tg\\TestSaveFile", 0);
//        nightlyFileDeletion.start();
//
//        RanameFile c = new RanameFile("C:\\Users\\User\\Documents\\תיכנות\\git\\inquirymanagement_tg\\newFolder", "prefix_");
//        c.start();

//        try {
//            ServerSocket serverSocket = new ServerSocket(12345);
//            InquiryManagerServer inquiryManagerServer = new InquiryManagerServer(serverSocket);
//            inquiryManagerServer.start();
//            System.out.println("Server is running...");
//        } catch (
//                IOException e) {
//            e.printStackTrace();
//        }
//
//
//        InquiryManager inquiryManager = InquiryManager.getInstance();
//
//        inquiryManager.startInquiryProcessing();
//
//        RepresentativeManager representativeManager = new RepresentativeManager();
//        representativeManager.representativeAction();

        try {
            InquiryManager inquiryManager = InquiryManager.getInstance();
            RepresentativeManager representativeManager = new RepresentativeManager();
            representativeManager.representativeAction();
            inquiryManager.processInquiryManager();
            inquiryManager.startInquiryProcessing();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
