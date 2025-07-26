package clientServer;

import java.io.IOException;
import java.net.ServerSocket;

public class InquiryManagerServer extends Thread {
    private ServerSocket myServer;

    public InquiryManagerServer(ServerSocket myServer) {

        this.myServer = myServer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    HandleClient handleClient = new HandleClient(myServer.accept());
                    handleClient.start();
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } finally {
            try {
                if (myServer != null && !myServer.isClosed()) {
                    myServer.close();
                    System.out.println("Server socket closed.");
                }
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
            }
        }
    }
}


