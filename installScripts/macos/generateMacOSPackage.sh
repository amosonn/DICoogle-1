## Wrote by: Luís A. Bastião Silva <bastiao@ua.pt>
## It's GPL :P

echo "Running..."
echo "Removing old build/dist.."


rm -rf dist build
echo "Removing old Dicoogle.app..."
rm -rf Dicoogle.app

echo "Compiling..."
echo "#######################"
ant
echo "Creating Dicoogle.app directories..."


echo "#######################"

mkdir -p Dicoogle.app/Contents
mkdir -p Dicoogle.app/Contents/MacOS
mkdir -p Dicoogle.app/Contents/Resources
mkdir -p Dicoogle.app/Contents/Resources/Java

echo "Copy files..."
echo "#######################"
cp libjnotify.dylib Dicoogle.app/Contents/Resources/Java
cp dist/Dicoogle.jar Dicoogle.app/Contents/Resources/Java
cp dist/lib/*.jar Dicoogle.app/Contents/Resources/Java
cp installScripts/macos/JavaApplicationStub Dicoogle.app/Contents/MacOS
cp installScripts/macos/Info.plist Dicoogle.app/Contents/
cp installScripts/macos/PkgInfo Dicoogle.app/Contents
cp installScripts/macos/DicoogleLogo.icns Dicoogle.app/Contents/Resources/


echo "Giving permissions..."
chmod +x Dicoogle.app/Contents/MacOS/JavaApplicationStub

hdiutil create Dicooglev0.4.dmg -srcfolder Dicoogle.app -volname "Dicoogle" -fs HFS+

echo "Cleaning ..."
rm -rf Dicoogle.app

echo "Done, thanks for your time. Now you can distribute Dicoogle PACS for Mac OS X (The better OS after Linux :P )"