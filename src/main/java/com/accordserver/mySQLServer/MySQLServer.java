package com.accordserver.mySQLServer;

import com.accordserver.ServerWindow;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MySQLServer {
    private static MySQLServer INSTANCE;
    final String serverPathStr = System.getenv("AppData") + "\\AccordServer\\";

    Thread processThread;
    Process mySQLProcess;

    private MySQLServer() {
    }

    public static MySQLServer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MySQLServer();
        }

        return INSTANCE;
    }

    // Method to boot MySQLServer
    public void init(ServerStartedListener serverStartedListener) {
        // Check if server is existing
        String zipUrl = "https://dev.mysql.com/get/Downloads/MySQL-8.1/mysql-8.1.0-winx64.zip";

        boolean firstStart = false;
        try {
            // Check if server need to be downloaded and installed
            if (!isServerInstalled()) {
                System.out.println("Downloading & installing Database...");
                ServerWindow.getInstance().printToConsole("Downloading & installing Database...");

                // Set firstStart to init server afterward
                firstStart = true;

                // Download and install server
                downloadAndExtractZip(zipUrl);

                // Setup server
                setupServer();

                System.out.println("Database downloaded & installed!");
                ServerWindow.getInstance().printToConsole("Database downloaded & installed!");
            } else {
                System.out.println("Database is installed!");
                ServerWindow.getInstance().printToConsole("Database is installed!");
            }
        } catch (IOException e) {
            System.out.println("Error downloading and installing Database: " + e.getMessage());
            ServerWindow.getInstance().printToConsole("Error downloading and installing Database: " + e.getMessage());
        }


        // Stuff only on first start
        if (firstStart) {
            // Init Server
            initServer();

            // Change password and create database
            changePasswordAndCreateDatabase();
        }

        // Start server
        startServer(serverStartedListener);
    }

    // Method to init the server
    private void initServer() {
        try {
            String mysqlPath = serverPathStr + "bin\\mysqld.exe";
            ProcessBuilder pb = new ProcessBuilder(mysqlPath, "--initialize", "--console");
            pb.redirectErrorStream(true);
            Process mySQLInitProcess = pb.start();

            // Print server output and check if server has initialized
            BufferedReader reader = new BufferedReader(new InputStreamReader(mySQLInitProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                // Check if server has initialized
                if (line.contains("Initialization - end")) {
                    System.out.println("Database initialized!");
                    ServerWindow.getInstance().printToConsole("Database initialized!");
                    mySQLInitProcess.destroy();
                    stopServer();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to set up the server
    private void changePasswordAndCreateDatabase() {
        try {
            String mysqlPath = serverPathStr + "bin\\mysqld.exe";
            String credentialsPath = serverPathStr + "credentials.txt";
            ProcessBuilder pb = new ProcessBuilder(mysqlPath, "--init-file=" + credentialsPath, "--console");
            pb.redirectErrorStream(true);
            Process mySQLChangePasswordProcess = pb.start();

            // Print server output and check if started
            BufferedReader reader = new BufferedReader(new InputStreamReader(mySQLChangePasswordProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                // Check if server is started
                if (line.contains("mysqld.exe: ready for connections")) {
                    // Password changed

                    // Create Database
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root&password=1234");
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("CREATE DATABASE Accord_DB");

                    System.out.println("Database has been set up!");
                    ServerWindow.getInstance().printToConsole("Database has been set up!");

                    mySQLChangePasswordProcess.destroy();
                    stopServer();
                    break;
                }
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to start the server
    private void startServer(ServerStartedListener serverStartedListener) {
        // Check if server is still running -> kill it
        ProcessHandle.allProcesses().forEach(process -> {
            if (process.info().command().toString().contains("mysqld")) {
                process.destroy();
            }
        });

        // Start server
        String mysqlPath = serverPathStr + "bin\\mysqld.exe";

        processThread = new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(mysqlPath, "--console");
                pb.redirectErrorStream(true);
                mySQLProcess = pb.start();

                // Print server output and check if started
                BufferedReader reader = new BufferedReader(new InputStreamReader(mySQLProcess.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
//                    System.out.println(line);
                    // Check if server is started
                    if (line.contains("mysqld.exe: ready for connections")) {
                        System.out.println("Database started! Located at: " + serverPathStr);
                        ServerWindow.getInstance().printToConsole("Database started! Located at: " + serverPathStr);

                        serverStartedListener.onServerStarted();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        processThread.start();
    }

    // Method to stop the server
    public void stopServer() {
        // Check if server is still running -> kill it
        ProcessHandle.allProcesses().forEach(process -> {
            if (process.info().command().toString().contains("mysqld")) {
                process.destroy();
            }
        });
    }

    // Method to check if the server is installed and set up
    public boolean isServerInstalled() {
        try {
            Path serverPath = Path.of(serverPathStr);
            Path dataPath = Path.of(serverPathStr + "data\\");
            return (Files.exists(serverPath) && Files.isDirectory(serverPath)) && Files.list(serverPath).findAny().isPresent() && (Files.exists(dataPath) && Files.isDirectory(dataPath)) && Files.list(dataPath).findAny().isPresent();
        } catch (IOException e) {
            return false;
        }
    }

    // Method to download and install the server
    public void downloadAndExtractZip(String zipUrl) throws IOException {
        // Check if some server data are there -> delete them
        if (Files.exists(Path.of(serverPathStr))) {
            FileUtils.deleteDirectory(new File(serverPathStr));
        }

        // Get server from url and extract it
        URL url = new URL(zipUrl);
        try (InputStream in = new BufferedInputStream(url.openStream()); ZipInputStream zipIn = new ZipInputStream(in)) {

            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                Path filePath = Path.of(serverPathStr, entry.getName().substring(entry.getName().indexOf("/")));
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.copy(zipIn, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipIn.closeEntry();
            }
        }
    }

    // Method to set up server files
    public void setupServer() throws IOException {
        // Copy & Set up Ini "my.ini" in server
        InputStream sourceIniStream = getClass().getClassLoader().getResourceAsStream("MySQLServer/my.ini");
        File targetIniConfig = new File(serverPathStr + "my.ini");
        try (OutputStream outputStream = new FileOutputStream(targetIniConfig)) {
            IOUtils.copy(Objects.requireNonNull(sourceIniStream), outputStream);
        }

        String newBaseDir = serverPathStr;
        String newDataDir = serverPathStr + "data\\";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(targetIniConfig));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("basedir")) {
                    line = "basedir=" + newBaseDir;
                }
                if (line.startsWith("datadir")) {
                    line = "datadir=" + newDataDir;
                }
                sb.append(line).append(System.lineSeparator());
            }
            reader.close();

            FileWriter writer = new FileWriter(targetIniConfig);
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Copy Root Credentials "credentials.txt" to server
        InputStream sourceCredentialsStream = getClass().getClassLoader().getResourceAsStream("MySQLServer/credentials.txt");
        File targetCredentialsConfig = new File(serverPathStr + "credentials.txt");
        try (OutputStream outputStream = new FileOutputStream(targetCredentialsConfig)) {
            IOUtils.copy(Objects.requireNonNull(sourceCredentialsStream), outputStream);
        }
    }
}
