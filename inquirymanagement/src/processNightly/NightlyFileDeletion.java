package processNightly;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class NightlyFileDeletion  extends Thread{
    String directoryPath;
    int days;

    public NightlyFileDeletion(String directoryPath, int days) {
        this.directoryPath = directoryPath;
        this.days = days;
    }

    @Override
    public void run() {
        try {
            deleteOldFiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public  void deleteOldFiles() throws IOException {
        File file=new File(directoryPath);
          if(file.exists()&&file.isDirectory()) {
              try {
                  File[] allFilesInTheFolder = file.listFiles();
                  for (File f :allFilesInTheFolder){
                      BasicFileAttributes basicFileAttributes= Files.readAttributes(f.toPath(), BasicFileAttributes.class);
                      Instant creationTime=basicFileAttributes.creationTime().toInstant();
                      LocalDateTime creationDateTime=  LocalDateTime.ofInstant(creationTime, ZoneId.systemDefault());
                      if(creationDateTime.isBefore(LocalDateTime.now().minusDays(days)))
                      {
                          f.delete();
                      }
                  }
              } catch (IOException e) {
                  throw new RuntimeException(e);
              }


          }

    }

}
