package com.distributedsystems;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

class Server_app extends Key_Store_Int_Imp{

    public static int server_num;
    public static int server_port;
    public static String server_name;

    public Server_app(int srvNum) throws RemoteException {
        super(srvNum);
    }

    public static void main(String args[]) throws Exception{
 
        if (args.length == 1){
            server_num = Integer.parseInt(args[0]);
        }
        else{
            System.out.print("Please enter a server number!!");
        }

        if (server_num == 1){                             // to create 5 instances of the server_application
            server_port = Global.server_1_name_port;      // with respect to the supplied command line args
            server_name = Global.server_1_name;
        }
        else if (server_num == 2)
        {
            server_port = Global.server_2_name_port;
            server_name = Global.server_2_name;
        }
        else if (server_num == 3)
        {
            server_port = Global.server_3_name_port;
            server_name = Global.server_3_name;
        }
        else if (server_num == 4)
        {
            server_port = Global.server_4_name_port;
            server_name = Global.server_4_name;
        }
        else if (server_num == 5)
        {
            server_port = Global.server_5_name_port;
            server_name = Global.server_5_name;
        }

        try {
            Server_app server = new Server_app(server_num);       // create object

            Key_Store_Int remote_srv_obj = (Key_Store_Int) UnicastRemoteObject.exportObject(server, 0);  // publish registry for the particular instance
            Registry registry = LocateRegistry.createRegistry(server_port);
            registry.bind(server_name, remote_srv_obj);

            System.out.println(server_name+ " UP AND RUNNING");

        } catch (Exception e) {
            System.out.println("Exception occurred: " + e);
        }

    }
}
