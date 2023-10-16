package com.accordserver;

import com.accordserver.accessingdatamysql.user.User;
import com.accordserver.accessingdatamysql.user.UserRepository;
import com.accordserver.controller.ChannelsController;
import com.accordserver.mySQLServer.MySQLServer;
import com.accordserver.mySQLServer.ServerStartedListener;
import com.accordserver.udpserver.UdpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AccordServerApplication {

    static ConfigurableApplicationContext configurableApplicationContext;
    @Autowired
    private static ApplicationContext context;

    public static void main(String[] args) {
        long serverStartTimeInMs = System.currentTimeMillis();

        // Show server window
        ServerWindow.getInstance().showWindow();
        ServerWindow.getInstance().setStatusTitle("Server is starting...");
        ServerWindow.getInstance().printToConsole("Server is starting...");

        // Add hook to handle shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Exit Spring Boot
            if (configurableApplicationContext != null) {
                SpringApplication.exit(configurableApplicationContext, () -> 0);
            }

            // Stop Database server
            MySQLServer.getInstance().stopServer();

            System.out.println("Server stopped!");
        }));


        // Start FIRST OF ALL the MySQLServer
        // Then start all other
        ServerStartedListener serverStartedListener = new ServerStartedListener() {
            @Override
            public void onServerStarted() {
                // start the SpringApplication with Rest-API and WebSockets
                configurableApplicationContext = SpringApplication.run(AccordServerApplication.class, args);

                // set all users offline
                UserRepository userRepository = configurableApplicationContext.getBean(UserRepository.class);
                for (User user : userRepository.findAll()) {
                    if (user.isOnline()) {
                        user.setOnline(false);
                        user.setUserKey(null);
                        userRepository.save(user);
                    }
                }

                // start the udpServer to route the audio packets AND set the udpServer to ChannelController to manage userArrived/Left
                UdpServer udpServer = new UdpServer();
                ChannelsController channelsController = configurableApplicationContext.getBean(ChannelsController.class);
                channelsController.setUdpServer(udpServer);


                // Server completly started
                long serverEndTimeInMs = System.currentTimeMillis();
                long serverBootDurationInMs = serverEndTimeInMs - serverStartTimeInMs;
                double seconds = serverBootDurationInMs / 1000.0;
                double roundedSeconds = Math.round(seconds * 100.0) / 100.0;
                ServerWindow.getInstance().setStatusTitle("Server started!");
                ServerWindow.getInstance().printToConsole("Server started! - Start time: " + roundedSeconds + " sec.");
                System.out.println("Accord-Server started!");
            }
        };
        // Start database
        MySQLServer.getInstance().init(serverStartedListener);
    }
}