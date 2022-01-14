package com.distributedsystems;

import java.rmi.Remote;
import java.util.ArrayList;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;

public interface Key_Store_Int extends Remote {

    String remote_put_pair (ArrayList request) throws Exception;
    String remote_get_pair(ArrayList request) throws Exception;
    String remote_del_pair(ArrayList request) throws Exception;

    String commit_phase(ArrayList request, int action) throws RemoteException, SocketTimeoutException;
    boolean prepare_phase(int request, ArrayList key, int action) throws RemoteException, SocketTimeoutException;
    boolean accept_phase(int request, ArrayList key, int action) throws RemoteException, SocketTimeoutException;

}
