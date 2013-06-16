; Java Launcher
;--------------

Name "Java Launcher"
Caption "Java Launcher"
Icon "DicoogleLogo.ico"
OutFile "Java Launcher.exe"

SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow

!define CLASSPATH ".;lib"
!define DICOOGLE "Dicoogle.jar"


; Definitions for Java 1.6 Detection
!define JRE_VERSION "1.6"
!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=18714&/jre-6u5-windows-i586-p.exe"


Section ""
  Call GetJRE
  Pop $R0

  ; Executing Dicoogle args
  StrCpy $0 '"$R0" -classpath "${CLASSPATH}" -jar ${DICOOGLE}'

  SetOutPath $EXEDIR
  Exec $0
SectionEnd

Function GetJRE
;
;  returns the full path of a valid java.exe
;  looks in:
;  1 - .\jre directory (JRE Installed with application)
;  2 - JAVA_HOME environment variable
;  3 - the registry
;  4 - hopes it is in current dir or PATH

  Push $R0
  Push $R1

  ; use javaw.exe to avoid dosbox.
  ; use java.exe to keep stdout/stderr
  !define JAVAEXE "javaw.exe"

  ClearErrors
  StrCpy $R0 "$EXEDIR\jre\bin\${JAVAEXE}"
  IfFileExists $R0 JreFound  ;; 1) found it locally
  StrCpy $R0 ""

  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\${JAVAEXE}"
  IfErrors 0 JreFound  ;; 2) found it in JAVA_HOME

  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\${JAVAEXE}"

  IfErrors 0 JreFound  ;; 3) found it in the registry
  StrCpy $R0 "${JAVAEXE}"  ;; 4) wishing you good luck

 JreFound:
  Pop $R1
  Exch $R0
FunctionEnd




Function GetJRE2
        MessageBox MB_OK "${PRODUCT_NAME} uses Java ${JRE_VERSION}, it will now \
                         be downloaded and installed"

        StrCpy $2 "$TEMP\Java Runtime Environment.exe"
        nsisdl::download /TIMEOUT=30000 ${JRE_URL} $2
        Pop $R0 ;Get the return value
                StrCmp $R0 "success" +3
                MessageBox MB_OK "Download failed: $R0"
                Quit
        ExecWait $2
        Delete $2
FunctionEnd


Function DetectJRE
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" \
             "CurrentVersion"
  StrCmp $2 ${JRE_VERSION} done

  Call GetJRE

  done:
FunctionEnd