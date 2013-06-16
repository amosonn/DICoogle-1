@echo off

echo ########################
echo # Dicoogle for Windows #
echo ########################
echo .



echo Setting installation variables...
set MakeNSIS="C:\Program Files (x86)\NSIS\makensis.exe"
set WinInstallDir=installScripts\windows
echo Builing source code


echo .
echo Creating installer...
xcopy %WinInstallDir%\DicoogleLogo.ico .

%MakeNSIS% /P5 /V4 /NOCD %WinInstallDir%\dicoogle_dist.nsi
%MakeNSIS% /P5 /V4 /NOCD %WinInstallDir%\setup.nsi


echo Clean stuff in Windows is not necessary because nobody devels under windows, right?
