
# java -jar Dicoogle.jar & 

# sleep 10

DATASET="/Volumes/HD1/DicoogleMigration/Dicoogle/trunk/Dicoogle/dataset/"
TARGET="193.136.171.92"
#TARGET="localhost"
PORT="6666"

# storescu -aet MYAET

# ./storescu -aet NIMBUS 193.136.171.92 5678 ~/Dicoogle/Imagens/RSI_Images/1.dcm
# ./storescu -aet NIMBUS 193.136.171.92 5678 ~/Dicoogle/Imagens/RSI_Images/1.dcm
echo $DATASET"PNEUMATIX"


dcmsnd DICOOGLE-STORAGE@$TARGET:$PORT $DATASET"test2"
#dcmsnd NIMBUS@193.136.171.92:5678 $DATASET"test"
