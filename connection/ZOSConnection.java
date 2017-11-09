package connection;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;

import java.io.*;

public class ZOSConnection {
    private String hostName;
    private boolean isLogin;
    private FTPClient ftpClient;
    private String userName;
    private String password;

    public ZOSConnection(String hostName) {
        this.hostName = hostName;
        ftpClient = new FTPClient();
        isLogin = false;
    }

    public void open() throws IOException {
        ftpClient.connect(hostName);
    }

    public void close() throws IOException {
        ftpClient.disconnect();
    }

    public boolean login(String userName, String password) throws IOException {
        boolean success = false;
        try {
            success = ftpClient.login(userName, password);
        } catch (FTPConnectionClosedException exception) {
            open();
            success = login(userName, password);
        }
        if (success) {
            this.userName = userName;
            this.password = password;
            isLogin = true;
        }
        return success;
    }

    public boolean logout() throws IOException {
        boolean success = false;
        try {
            success = ftpClient.logout();
        } catch (FTPConnectionClosedException exception) {
            open();
            success = logout();
        }
        if (success) {
            isLogin = false;
        }
        return success;
    }

    public boolean get(String remoteFileName, String localFileName) throws IOException {
        boolean success = false;
        try (OutputStream outputStream = new FileOutputStream(localFileName)) {
            success = ftpClient.retrieveFile(remoteFileName, outputStream);
        } catch (FTPConnectionClosedException exception) {
            open();
            if(isLogin) {
                login(userName, password);
            }
            success = get(remoteFileName, localFileName);
        }
        return success;
    }

    public boolean put(String remoteFileName, String localFileName) throws IOException {
        boolean success = false;
        try(InputStream inputStream = new FileInputStream(localFileName)){
            success = ftpClient.storeFile(remoteFileName, inputStream);
        } catch (FTPConnectionClosedException exception) {
            open();
            if(isLogin) {
                login(userName, password);
            }
            success = put(remoteFileName, localFileName);
        }
        return success;
    }
    
    public boolean write(String remoteFileName, InputStream content) throws IOException {
    	boolean success = false;
        try{
            success = ftpClient.storeFile(remoteFileName, content);
        } catch (FTPConnectionClosedException exception) {
            open();
            if(isLogin) {
                login(userName, password);
            }
            success = write(remoteFileName, content);
        }
        return success;
    }

    public boolean submitJob(String remoteFileName, String localFileName) throws IOException {
        boolean success = false;
        try {
            ftpClient.sendSiteCommand("filetype=jes");
            success = get(remoteFileName, localFileName);
            ftpClient.sendSiteCommand("filetype=seq");
        } catch (FTPConnectionClosedException exception) {
            System.out.println("I am here!");
            open();
            if(isLogin) {
                login(userName, password);
                success = submitJob(remoteFileName, localFileName);
            }
        }
        return success;
    }

    public boolean changeJesJobName(String newJesJobName) throws IOException {
        return ftpClient.sendSiteCommand("jesjobname="+newJesJobName );
    }


    public int getReplyCode() {
        return ftpClient.getReplyCode();
    }
    public String getReplyString() {
        return ftpClient.getReplyString();
    }

}