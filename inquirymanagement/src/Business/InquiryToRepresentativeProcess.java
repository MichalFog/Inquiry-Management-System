package Business;
import Data.Inquiry;
import Data.Representative;
import java.io.File;
import java.io.IOException;

public class InquiryToRepresentativeProcess extends Thread {

    private final InquiryManager inquiryManager = InquiryManager.getInstance();

    @Override
    public void run() {
        while (true) {
            try {
                if (!RepresentativeManager.availableRepresentatives.isEmpty() && (!InquiryManager.allInquiries.isEmpty() || !InquiryManager.inquiriesAtHandling.isEmpty())) {

                    Inquiry inquiry = InquiryManager.allInquiries.poll();
                    Representative rep = RepresentativeManager.availableRepresentatives.poll();

                    if (inquiry != null && rep != null) {
                        inquiry.setRepresentativeCode(rep.getCode());
                        inquiry.setStatusInquiry(clientServer.StatusInquiry.HANDLING);
                        InquiryManager.inquiriesAtHandling.add(inquiry);

                        inquiryManager.handleFiles.saveCSV(inquiry, inquiryManager.handleFiles.getDirectoryPath(inquiry));
                        System.out.println("פנייה " + inquiry.getCode() + " הועברה ל-HANDLING");

                        Thread.sleep(5000);

                        inquiry.setStatusInquiry(clientServer.StatusInquiry.INHISTORY);
                        String historyFilePath = inquiryManager.createHistoryFile(inquiry);
                        inquiryManager.handleFiles.saveCSV(inquiry, historyFilePath);
                        InquiryManager.inquiriesAtHandling.remove(inquiry);

                        File originalFile = new File(inquiryManager.handleFiles.getDirectoryPath(inquiry));
                        if (originalFile.exists() && !originalFile.delete()) {
                            System.out.println("Error deleting original file: " + originalFile.getPath());
                        }

                        System.out.println("פנייה " + inquiry.getCode() + " הועברה להיסטוריה");

                        // מחזירים את הנציג לרשימת הזמינים
                        RepresentativeManager.availableRepresentatives.add(rep);
                    }

                    // טעינת נציג גיבוי
                    if (RepresentativeManager.availableRepresentatives.isEmpty() && !InquiryManager.representativesList.isEmpty() && InquiryManager.allInquiries.size() > 3) {

                        Representative backupRep = InquiryManager.representativesList.removeFirst();
                        if (backupRep != null) {
                            RepresentativeManager.availableRepresentatives.add(backupRep);
                        }
                    }
                }

                Thread.sleep(1000); // הפחתת עומס על המעבד

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

