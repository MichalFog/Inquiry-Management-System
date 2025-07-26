package HandleStoreFiles;

import Data.Inquiry;

import java.io.IOException;
import java.util.List;

public interface ForSaving {
    public String getFolderName();
    public String getFileName();
    public String getData();
     public  Inquiry parseFromFile(List<String> values);
}
