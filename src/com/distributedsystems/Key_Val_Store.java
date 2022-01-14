package com.distributedsystems;

import static com.distributedsystems.Global.provide_server;
import java.net.SocketTimeoutException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;


class Key_Value_Store {
    public HashMap<String, String> store;

    public Key_Value_Store(){
        store = new HashMap();
    }

    public String put_pair(ArrayList request) {                             //function to store in hmap

        String response;
        if (!store.containsKey((String) request.get(1))) {
            store.put((String) request.get(1), (String) request.get(2));

            response = "ACK-Added pair!" + " Data-Store contents: " + store;
            System.out.println(response + " at timestamp: " + Global.FORMATTER.format(new Date().getTime()));
        } else {
            response = "ERR-Could not add Key, already exists!";
            System.out.println("ERR-Could not add Key, already exists!");
        }
        return response;
    }


    public String get_pair(ArrayList request) {                    //function to fetch pair from hmap

        String k = (String) request.get(1);
        String v;
        String response;

        if (store.containsKey(k)){
            v = store.get(k);

            response = "ACK-Fetched pair! " + k + ":" + v;
            System.out.println(response + " at timestamp: " + Global.FORMATTER.format(new Date().getTime()));

        }
        else{
            System.out.println("ERR-Could not find Key to get, DNE!");
            response = "ERR-Could not find Key to get, DNE!";           //send response to the client
        }
        return response;
    }


    public String delete_pair(ArrayList request) {
        String response;

        if (store.containsKey((String) request.get(1))){
            store.remove((String) request.get(1));

            response = "ACK-Deleted pair with key! "+request.get(1) + " Data-Store contents: " + store;
            System.out.println(response + " at timestamp: " + Global.FORMATTER.format(new Date().getTime()));
        }
        else{                                       //send response to the client, else return and log an error
            System.out.println("ERR-Could not find Key to delete, DNE!");
            response = "ERR-Could not find Key to delete, DNE!";
        }
        return response;
    }
}

class Learner extends Key_Value_Store implements Runnable{

    public void start(){
    }

    public String commit(ArrayList request, int action){      //call commit with respect to the action number
        String response = "";
        switch(action) {
            case 1: response = super.get_pair(request);
                break;
            case 2: response = super.put_pair(request);
                break;
            case 3: response = super.delete_pair(request);
                break;
        }
        return response;
    }

    @Override
    public void run() {
    }
}


class Acceptor extends Key_Value_Store implements Runnable{

    private static int mypId;
    private boolean active;
    private int srvNum;

    public void setMyproposalId(int myproposalId) {
        Acceptor.mypId = myproposalId;
    }

    public void start(){
        active = true;
    }

    public boolean accept(int proposalId, ArrayList request, int action) {
        return check(proposalId, request, action);
    }

    public boolean prepare(int proposalId, ArrayList request, int action) {
        return check(proposalId, request, action);
    }

    private boolean check(int proposalId, ArrayList request, int action){
        try{                                                             // initiating a random failure as per the question
            if(((int)((Math.random()*Global.NUMBER_OF_SERVERS)+1)) == srvNum){
                System.out.println("Making Server"+srvNum+" unavailable temporarily");
                Thread.sleep(500);
            }
        } catch (InterruptedException ie){

        }                                                           //check if received proposal id is greater than the current

        if(proposalId < mypId){
            return false;
        }
        else{
            setMyproposalId(proposalId);
            return true;
        }

    }
    public void setServerNumber(int serverNumber) {
        this.srvNum = serverNumber;
    }

    @Override
    public void run() {
    }
}


class Proposer extends Key_Value_Store implements Runnable{

    private static int proposalId;
    private ArrayList value;

    public Proposer(){
        super();
    }

    public void setValue(ArrayList request) {
        this.value = request;
    }

    public void start(){
        proposalId = 0;
    }

    public synchronized String propose(ArrayList request, int action) {
        String response = "";
        setValue(request);
        Map<String, String> serverMap = provide_server();
        Registry registry = null;
        proposalId = proposalId + 1;
        int response_count = 0;

        try{

            for(Map.Entry<String, String> entry : serverMap.entrySet()){       //iterate over all servers in the list
                try{
                    registry = LocateRegistry.getRegistry(entry.getValue(), Global.provide_port(entry.getKey()));
                    Key_Store_Int stub = (Key_Store_Int) registry.lookup(entry.getKey());
                    if(stub.prepare_phase(proposalId, request, action)){      //run prepare phase
                        response_count++;
                    }
                }
                catch(SocketTimeoutException se){
                    continue;
                }catch(RemoteException re){
                    continue;
                }
            }

            if(response_count > Global.NUMBER_OF_SERVERS / 2){         //check for majority
                System.out.println(response_count + " servers responsed to the prepare phase");
                response_count = 0;
                for(Map.Entry<String, String> entry : serverMap.entrySet()){
                    try{
                        registry = LocateRegistry.getRegistry(entry.getValue(), Global.provide_port(entry.getKey()));
                        Key_Store_Int stub = (Key_Store_Int) registry.lookup(entry.getKey());

                        if(stub.accept_phase(proposalId, request, action)){     //run accept phase
                            response_count++;
                        }
                    }catch(SocketTimeoutException se){
                        continue;
                    }catch(RemoteException re){
                        continue;
                    }
                }
            } else {
                response = "Consensus could not achieved, only " + response_count + "servers responded in the prepare phase";
                System.out.println(response);
                return response;
            }

            if(response_count > (Global.NUMBER_OF_SERVERS / 2)){            // check for majority
                System.out.println(response_count + " servers responded in the accept phase");
                for(Map.Entry<String, String> entry : serverMap.entrySet()){
                    try{
                        registry = LocateRegistry.getRegistry(entry.getValue(), Global.provide_port(entry.getKey()));
                        Key_Store_Int stub = (Key_Store_Int) registry.lookup(entry.getKey());

                        response = stub.commit_phase(request, action);     // if majority we perform a commit on the key-store
                    }catch(SocketTimeoutException se){

                        continue;
                    }catch(RemoteException re){

                        continue;
                    }
                }
            } else {
                response = "Consensus could not be achieved, only " + response_count + "servers responded to the accept phase";
                System.out.println(response);
                return response;
            }
        }
        catch(NotBoundException nbe){
            System.out.println("Exception occurred: " + nbe);
        }
        return response;
    }

    @Override
    public void run() {
    }
}