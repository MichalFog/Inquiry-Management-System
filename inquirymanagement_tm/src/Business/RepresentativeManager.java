package Business;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import Data.Representative;
import HandleStoreFiles.HandleFiles;
import clientServer.InquiryManagerServer;

import static Business.InquiryManager.nextCodeVal;
import static Business.InquiryManager.representativesList;

public class RepresentativeManager {
    static Queue<Representative> availableRepresentatives = new ConcurrentLinkedQueue<>();
    HandleFiles handleFiles = new HandleFiles();

    public void representativeAction() throws IOException {
        int val=handleFiles. readCaseNumber();
        if(nextCodeVal<val)
            nextCodeVal=val;

        Scanner scanner = new Scanner(System.in);
        int choose;

        do {
            System.out.println("Enter 1 to add representative, 2 to delete representative, 3 to launch referral system or -1 to exit");
            choose = scanner.nextInt();
            scanner.nextLine(); // ניקוי שורת הקלט

            switch (choose) {
                case 1:
                    defineRepresentative(scanner);
                    break;

                case 2:
                    System.out.println("Enter tz of the representative:");
                    String tz = scanner.nextLine();
                    deleteRepresentative(tz);
                    break;

                case 3:
                    for (Representative representative : representativesList) {
                        System.out.println("If " + representative + " is available press 1 else press 0");
                        int ifAvailable = scanner.nextInt();
                        if (ifAvailable == 1)
                            availableRepresentatives.add(representative);
                    }
                    // הפעלת המערכת
                    try {
                        ServerSocket serverSocket = new ServerSocket(12345);
                        InquiryManagerServer inquiryManagerServer = new InquiryManagerServer(serverSocket);
                        inquiryManagerServer.start();
                        System.out.println("Server is running...");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    choose = -1;
                    break;

                case -1:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice, please try again.");
                    break;
            }
        } while (choose != -1);

        scanner.close();
    }

    public Representative defineRepresentative(Scanner scanner) throws IOException {
        System.out.println("Enter your name:");
        String nameAgent = scanner.nextLine();

        System.out.println("Enter ID:");
        String ID = scanner.nextLine();

        Representative newRepresentative = new Representative(nameAgent, ID);
        handleFiles.saveCSV(newRepresentative, "Representative/" + newRepresentative.getCode() + ".csv");
        representativesList.add(newRepresentative);
        System.out.println("Representative added.");
        return newRepresentative;
    }

    public void deleteRepresentative(String tz) {
        // מחיקת נציג מהרשימה הכללית
        Iterator<Representative> iterator = representativesList.iterator();
        while (iterator.hasNext()) {
            Representative rep = iterator.next();
            if (rep.getTz().equals(tz)) {
                iterator.remove();
            }
        }

        // מחיקת נציג מתור הנציגים הזמינים
        Queue<Representative> filteredQueue = new ConcurrentLinkedQueue<>();
        while (!availableRepresentatives.isEmpty()) {
            Representative current = availableRepresentatives.poll();
            if (!current.getTz().equals(tz)) {
                filteredQueue.add(current);
            }
        }
        availableRepresentatives = filteredQueue;

        // מחיקת קובץ ה-CSV של הנציג
        Path directory = Paths.get("Representative");
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            System.err.println("Directory does not exist: " + directory.toString());
            return;
        }

        try (Stream<Path> filePaths = Files.list(directory)) {
            filePaths.filter(Files::isRegularFile).forEach(entry -> {
                try {
                    if (checkConditionForDeleteFile(entry, tz)) {
                        Files.delete(entry);
                        System.out.println("Deleted file: " + entry.getFileName());
                    }
                } catch (IOException e) {
                    System.err.println("Error reading file: " + entry.getFileName() + ". " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.err.println("Error accessing directory: " + e.getMessage());
        }
    }

    // בודק האם הקובץ מכיל את ה-tz המבוקש
    private boolean checkConditionForDeleteFile(Path filePath, String targetValue) {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length > 3 && columns[3].trim().equals(targetValue)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath.getFileName() + ". " + e.getMessage());
        }
        return false;
    }
}
