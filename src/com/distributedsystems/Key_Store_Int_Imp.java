package com.distributedsystems;

import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Key_Store_Int_Imp implements Key_Store_Int{

    private Acceptor acceptor_role;
    private Proposer proposer_role;                // definition of the remote methods in the interface
    private Learner learner_role;
    

    public Key_Store_Int_Imp(int srvNum) throws RemoteException {
        learner_role = new Learner();
        acceptor_role = new Acceptor();
        proposer_role = new Proposer();
        proposer_role.start();
        learner_role.start();
        acceptor_role.start();
        acceptor_role.setServerNumber(srvNum);
    }

    public String remote_get_pair(ArrayList request) {
        return proposer_role.propose(request, 1);
    }

    public String remote_put_pair(ArrayList request) {
        return proposer_role.propose(request, 2);
    }

    public String remote_del_pair(ArrayList request){
        return proposer_role.propose(request, 3);
    }

    @Override
    public boolean prepare_phase(int pId, ArrayList request, int action) throws RemoteException, SocketTimeoutException {
        return acceptor_role.prepare(pId, request, action);
    }

    @Override
    public boolean accept_phase(int pId, ArrayList request, int action) throws RemoteException, SocketTimeoutException  {
        return acceptor_role.accept(pId, request, action);
    }

    @Override
    public String commit_phase(ArrayList request, int action) throws RemoteException {
        return learner_role.commit(request, action);
    }
}