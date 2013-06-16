
java -jar Dicoogle.jar & 

sleep 10


TARGET="193.136.171.92"
#TARGET="localhost"
PORT="6666"
AETITLE="DICOOGLE-STORAGE"


dcmqr $AETITLE@$TARGET:$PORT -qPatientName=A* -repeat 30
dcmqr $AETITLE@$TARGET:$PORT -qPatientName=A* -repeat 30
dcmqr $AETITLE@$TARGET:$PORT -qPatientName=A* -repeat 30
dcmqr $AETITLE@$TARGET:$PORT -qPatientName=A* -repeat 30
