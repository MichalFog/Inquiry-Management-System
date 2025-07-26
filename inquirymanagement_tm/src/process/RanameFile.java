package process;
import java.io.File;


public class RanameFile extends  Thread{


             private String path;
             private String nameFile;

             public RanameFile(String path, String nameFile) {
                 this.path = path;
                 this.nameFile = nameFile;
             }

             public String getPath() {
                 return path;
             }

             public void setPath(String path) {
                 this.path = path;
             }

             public String getNameFile() {
                 return nameFile;
             }

             public void setNameFile(String nameFile) {
                 this.nameFile = nameFile;
             }
             @Override
             public void run(){
                 File folder =new File(path);
                 if(!folder.exists()||!folder.isDirectory()){
                     System.out.println("is not exist this file");

                 }

                 File [] files= folder.listFiles();
                 if(files==null)
                     return;
                 for(File file:files){
                     if(file.isFile()) {
                         File f = new File(path + "\\" + nameFile + file.getName());

                         boolean flag=file.renameTo((f));
                         if(flag){
                             System.out.println("rename from"+ file.getName()+" to "+f.getName());
                         }
                         else {
                             System.out.println("error to rename file");
                         }
                     }

                 }
             }


         }



