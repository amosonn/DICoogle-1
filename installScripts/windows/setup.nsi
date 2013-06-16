; Starting with Vista we have to explicitly mark the required execution level
; for the installer. Not doing so will cause Windows to apply some backward
; compatibility changes during the installation process, like automatically
; moving any shortcuts created in the user's start menu to all users'
; start menu, causing the uninstaller to not remove these entries.
RequestExecutionLevel admin

!define APPLICATION_NAME "Dicoogle"
!define APPLICATION_VERSION "0.4"

Name "${APPLICATION_NAME}"
InstallDir "$PROGRAMFILES\${APPLICATION_NAME}\"

!include "MUI.nsh"
; MUI Settings:

; Pages definitions

;!define MUI_PAGE_HEADER_TEXT "Dicoogle PACS - A distributed medical image system"
!define MUI_PAGE_HEADER_SUBTEXT "Dicoogle"

; Finish page definitions
!define MUI_FINISHPAGE_LINK "Don't forget to visit Dicoogle's website!"
!define MUI_FINISHPAGE_LINK_LOCATION "http://www.dicoogle.com/"

Outfile ${APPLICATION_NAME}-${APPLICATION_VERSION}.exe

; MUI Installer Pages
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "licence-Dicoogle.txt"
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

; MUI Uninstaller Pages
;!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

; Language
!insertmacro MUI_LANGUAGE "English"

; Registry key used by Dicoogle for adding information in "Add/Remove Programs"
!define AddRemKey \
    "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_NAME} ${APPLICATION_VERSION}"

Section "Dicoogle" SecDicoogle
  SetOutPath $INSTDIR
  File licence-Dicoogle.txt
  File "Java Launcher.exe"
  File "DicoogleLogo.ico"
  File udp.xml
  File /r dist\*.*
  CreateDirectory "$SMPROGRAMS\Dicoogle"
  CreateShortCut "$SMPROGRAMS\Dicoogle\Dicoogle.lnk" "$INSTDIR\Java Launcher.exe" "" $INSTDIR\DicoogleLogo.ico
  CreateShortCut "$SMPROGRAMS\Dicoogle\Dicoogle-Uninstaller.lnk" "$INSTDIR\Dicoogle-Uninstaller.exe" "" $INSTDIR\Dicoogle-Uninstaller.exe


  ; shortcuts for DicoogleClient
  CreateShortCut "$INSTDIR\Dicoogle Client.lnk" "$INSTDIR\Dicoogle.jar" \
    "-c" "" "" SW_SHOWNORMAL "" "Dicoogle Client"
  CreateShortCut "$SMPROGRAMS\Dicoogle\Dicoogle Client.lnk" \
    "$INSTDIR\Dicoogle.jar" "-c" "" "" SW_SHOWNORMAL "" \
    "Dicoogle Client"

  ; shortcuts for DicoogleServer
  CreateShortCut "$INSTDIR\Dicoogle Server.lnk" "$INSTDIR\Dicoogle.jar" \
    "-s" "" "" SW_SHOWNORMAL "" "Dicoogle Server"
  CreateShortCut "$SMPROGRAMS\Dicoogle\Dicoogle Server.lnk" \
    "$INSTDIR\Dicoogle.jar" "-s" "" "" SW_SHOWNORMAL "" \
    "Dicoogle Server"

  WriteUninstaller "$INSTDIR\Dicoogle-Uninstaller.exe"


  ; Add uninstall information to "Add/Remove Programs"
  WriteRegStr HKLM '${AddRemKey}' "DisplayName" ${APPLICATION_NAME}
  WriteRegStr HKLM '${AddRemKey}' "DisplayVersion" ${APPLICATION_VERSION}
  WriteRegStr HKLM '${AddRemKey}' "DisplayIcon" "$INSTDIR\DicoogleLogo.ico"
  WriteRegStr HKLM '${AddRemKey}' "UrlInfoAbout" "http://www.dicoogle.com"
  WriteRegStr HKLM '${AddRemKey}' "UninstallString" "$INSTDIR\Dicoogle-Uninstaller.exe"
SectionEnd


; Components descriptions
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT SecDicoogle "Install Dicoogle..."
!insertmacro MUI_FUNCTION_DESCRIPTION_END

Section "Uninstall"
    RMDir /r "$SMPROGRAMS\Dicoogle"
    RMDir /r "$INSTDIR"

    ; Remove the key created for adding uninstall information
    DeleteRegKey HKLM '${AddRemKey}'
SectionEnd

