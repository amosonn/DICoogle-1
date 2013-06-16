## Wrote by: Luís A. Bastião Silva <bastiao@ua.pt>
## It's GPL :P


DICOOGLE_SRC="Dicoogle_v0.4-source"
DICOOGLE_BIN="Dicoogle_v0.4-independent"
echo "Running..."
echo "Removing old build/dist.."


rm -rf dist build

echo "Compiling..."
echo "#######################"
ant
echo "Creating directories..."


echo "#######################"

mkdir -p $DICOOGLE_BIN
mkdir -p $DICOOGLE_BIN/lib

echo "Copy files..."
echo "#######################"


#Binaries
cp dist/Dicoogle.jar $DICOOGLE_BIN/
cp *.dll $DICOOGLE_BIN/
cp *.so $DICOOGLE_BIN/
cp libjnotify.dylib $DICOOGLE_BIN/

cp udp.xml $DICOOGLE_BIN/
cp tags.xml $DICOOGLE_BIN/


cp dist/lib/*.jar $DICOOGLE_BIN/lib/
zip -9 -r $DICOOGLE_BIN.zip $DICOOGLE_BIN

#Sources
svn export https://bioserver2.ieeta.pt:444/svn/Dicoogle/branches/Dicoogle-v0.4 $DICOOGLE_SRC
tar cvfz $DICOOGLE_SRC.tar.gz $DICOOGLE_SRC

echo "Cleaning ..."

