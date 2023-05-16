# Key-Value-Store-using-PAXOS-consensus (2021)

> Tools - Java, Shell Scripts, Docker

Key-Value-Store based on a Client-Server architecture, consisting of multiple servers, using PAXOS consensus algorithm. The server applications are multithreaded for added concurrency and can handle multiple clients at once.

## About
Upon execution, the pre-defined operations are performed and then the user is asked for the input of whatever it wants from the list of choices. The user has the option to perform three operations on the server. The user input is received at the server. If the received request is for a ‘put’, ‘get’ or a ‘delete’ operation, then the request is forwarded to the coordinator. The server upon receiving the request from the client, initiates a round of Paxos, being the proposer. If all the servers are ready, then in the next phase, a commit operation is performed to all the instances of the server. This ensures that the data is consistent across all the server nodes. If the message is invalid, a log is made, and the client moves on to providing the next input. If the message is valid, the respective operation is forwarded and performed on the key-value store as directed by the user input. The acknowledgement of the same is sent back to the client, and the client logs the same on its side. If the transaction fails for some reason, or the consensus is not achieved, then the respective response is also sent back to the client via the server, the client is connected to. The remote methods are made available between all the participants using Java RMI and multithreading is also used to handle multiple concurrent requests. When all the servers are up, they in turn, publish a registry each. A separate class, which serves as a common store for each instance defines a hashmap as the store.

## Project Structure
Under the package in the folder, the following files exist:
- *Server_app.java* This file contains the java code for the server application and can be instantiated multiple times as per the number of servers required. This file takes in one command-line argument in the following format: `java com.distributedsystems.Server_app <server_number>`

- *Key_Store_Int.java* Contains the java code for the definition of the interface for the Key-value store. This file supplements the Server java file.

- *Key_Store_Int_Imp.java* Contains the java code for the implementation of the interfaces declared in the above file. The client uses the remote methods of this interface to access the servers for needed operations. This file supplements the Server java file. This file also defines remote methods for the Paxos phases.

- *Key_Val_Store.java* Contains the java code for the implementation of the class containing the key-value store and the acceptor, learner and proposer classes.

- *Global.java* Contains the java code for declaring all constants that have been used across the entire application. This provides as a common lookup for the addresses and ports of the servers.

- *Client.java* This file contains the java code for the client application. This application takes input from the user for the operations to be performed on the key-value store. This file is preloaded with 5 put, get, and delete operations. The client application can connect to any of the five servers as desired. This file takes in two command-line arguments in the following format: `java com.distributedsystems.Client <server_addresss> <server_PQDNS>`

The root of the project contains the following files necessary for execution of the project on Docker:
- *Dockerfile* This contains the parameters to generate and create images of the builds for the servers and the client. JDK 17 from the alpine image has been used.

- *server{1-5}.sh* These files together contain the instructions for starting up individual servers as separate containers. Note that the default 'host' network has been used to avoid exposing any ports, as, on a custom network, the required ports need to be exposed manually. This has been done with the aim to reduce any complexities during the execution. These 5 files need to be run first before starting up the client. Also, server1.sh needs to be executed first. The files take in one command-line argument, each in the following format: `./server{1-5}.sh <server_number>`

- *client.sh* This file contains all the configurations needed to start the client container that has been created in the previous step. The client container also gets attached to the default 'host' network. This file needs to be executed once the five servers are up and running. This file takes in two command-line arguments in the following format: `./client.sh <server_address> <server_PQDNS>`

## Sample Execution
The commands are to be executed in order and on separate Linux shells, so in all 6 terminals windows will be required. The respective logs fall inside the docker images and note, that the scripts will need the command line arguments to run. The execution for the project is as follows.

- Executing the application using docker and Linux Shell:
  - `./server1.sh 1`
  - `./server2.sh 2`
  - `./server3.sh 3`
  - `./server4.sh 4`
  - `./server5.sh 5`
  - `./client.sh localhost Server1@Paxos`
  - The respective logs fall inside the docker images.

**Server Script output**
<p align="center">
<img src="https://github.com/divitvasu/Key-Value-Store-using-PAXOS-consensus/assets/30820920/c404e16d-31f2-4968-99bc-86a0e19eaf63" alt="Image" width="700" height="500">
<img src="https://github.com/divitvasu/Key-Value-Store-using-PAXOS-consensus/assets/30820920/832b2590-19f8-4079-8439-90869d61bcf7" alt="Image" width="700" height="200">
</p>

**Client Script output**
<p align="center">
<img src="https://github.com/divitvasu/Key-Value-Store-using-PAXOS-consensus/assets/30820920/795f275c-d39a-432f-9664-96d82cc2285f" alt="Image" width="600" height="500">
</p>

## Concluding Thoughts
A server upon receiving the client request, becomes the proposer and initiates the round of Paxos. If the consensus can be reached, the proposer moves with the commit phase. During the propose/prepare phase, if majority of the servers don’t respond with a confirmation, the transaction is truncated. Else, the value is committed into the key-store. Also, an implementation for random failure has been done for testing the working. Whenever the acceptor class is called, the thread for that class is made to sleep randomly. Thus, each server experiences a random sleep time and the Paxos round still moves on. The entire aim of the consensus algorithm is focused on introducing fault tolerance to the two-phase commit protocol. A major difference between the two-phase protocol and Paxos is that the coordinator’s role is taken up by the servers in the distributed system and the one server who receives the request from the client becomes the proposer. There are three phases involved in Paxos: Propose, Accept, Learn.

A livelock condition could potentially occur, if there are a lot of incoming requests from other replicas. In such a case, all new requests would end up getting rejected. To resolve this a new proposer needs to be elected. This can be done using leader election algorithms such as Bully and Ring. This is also called Multi Paxos. This has not been included in the scope of this project. That is an additional implementation which could be made to make the project more robust. Also, this project checks for the data packet's validity on the client node itself. This saves a lot of bouncing requests, in case the data packet entered by the user is invalid. The code has been tested to not reach consensus without majority of the servers agreeing or being available. Also, whenever the nodes are brought up again, the Paxos implementation, proceeds as required. Also, the above implementation would terminate if the server node that the client is immediately connected to, fails. However, the failure with other replicas gets handled. The replica that the client is desired to be connected to can be provided to the client as command line arguments when starting it up. The code is fixed for working with five replicas. However, this can very well be made to be dynamic, by asking the user for the number of replicas to work with. The project could have also used a concurrent hashmap in lieu of the simple implementation, for added concurrency.

## References
- [JAVA RMI](https://www.javatpoint.com/RMI)
- [Multi-threading in Java](https://www.geeksforgeeks.org/multithreading-in-java/)
- [Paxos Consensus](https://medium.com/designing-distributed-systems/paxos-a-distributed-consensus-algorithm-41946d5d7d9)
- [Paxos Algorithm](https://people.cs.rutgers.edu/~pxk/417/notes/paxos.html)
- Distributed Systems – Concepts and Design – George Coulouris, Jean Dollimore, Tim Kindberg, Gordon Blair
