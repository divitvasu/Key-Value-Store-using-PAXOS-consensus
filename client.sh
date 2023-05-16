CLIENT_IMAGE='client'
PROJECT_NETWORK='host'
CLIENT_CONTAINER='client-1'

#if [ $# -ne 3 ]
#then
#  echo "Usage: ./run_client.sh <container-name> <port-number> <protocol>"
#  exit
#fi

#if [ $# -ne 3 ]
#then
#  echo "Usage: ./tcpclient.sh <server address> <port-number>"
#  exit
#fi

# run the image and open the required ports
echo "----------Running client app----------"
docker run -it --rm --name $CLIENT_CONTAINER --network $PROJECT_NETWORK $CLIENT_IMAGE java com.distributedsystems.Client "$1" "$2"

# run client docker container with cmd args
#docker run -it --rm --name "$1" \
# --network $PROJECT_NETWORK $CLIENT_IMAGE \
# java client.ClientApp $SERVER_CONTAINER "$2" "$3"
 
#echo "----------watching logs from client----------"
#docker logs $CLIENT_CONTAINER -f

