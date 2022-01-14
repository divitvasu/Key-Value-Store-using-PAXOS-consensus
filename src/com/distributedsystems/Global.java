package com.distributedsystems;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class Global {               // file defining constant values for access across all classes

    public static String server_1_name = "Server1@Paxos";
    public static int server_1_name_port = 6060;
    public static String server_2_name = "Server2@Paxos";
    public static int server_2_name_port = 6061;
    public static String server_3_name = "Server3@Paxos";
    public static int server_3_name_port = 6062;
    public static String server_4_name = "Server4@Paxos";
    public static int server_4_name_port = 6063;
    public static String server_5_name = "Server5@Paxos";
    public static int server_5_name_port = 6044;
    static int NUMBER_OF_SERVERS = 5;
    static DateFormat FORMATTER = new SimpleDateFormat("HH:mm:ss:SSS");

    public static int provide_port(String value) {
        if(value.equals(server_1_name)){
            return server_1_name_port;    // return the port number for the queried server
        }
        if(value.equals(server_2_name)){
            return server_2_name_port;
        }
        if(value.equals(server_3_name)){
            return server_3_name_port;
        }
        if(value.equals(server_4_name)){
            return server_4_name_port;
        }
        if(value.equals(server_5_name)){
            return server_5_name_port;
        }
        return 0;
    }

    public static Map<String, String> provide_server() {
        Map<String, String> server_details = new HashMap<String, String>();
        server_details.put(server_1_name, "localhost");   // return the address for the queried server
        server_details.put(server_2_name, "localhost");
        server_details.put(server_3_name, "localhost");
        server_details.put(server_4_name, "localhost");
        server_details.put(server_5_name, "localhost");
        return server_details;
    }

}