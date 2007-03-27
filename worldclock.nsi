; Initial Script generated by the HM NIS Edit Script Wizard.

; HM NIS Edit Wizard helper defines
!define PRODUCT_NAME "World Clock"
!define PRODUCT_VERSION "0.5"
!define PRODUCT_PUBLISHER "Ludovic HOCHET"
!define PRODUCT_WEB_SITE "https://worldclock-application.dev.java.net/"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"

; MUI 1.67 compatible ------
!include "MUI.nsh"

; MUI Settings
!define MUI_ABORTWARNING
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"

; Required Java version
!define JAVA_MAJOR 1
!define JAVA_MINOR 6

; Functions

; GetJavaVersion
;
; Returns on top of stack
; version number (If JRE installed)
; or
; '' (If JRE not installed)
;
; Usage:
;   Call GetJavaVersion
;   Pop $R0
;   ; at this point $R0 is version number
Function GetJavaVersion
   Push $R0
   ClearErrors
   ReadRegStr $R0 HKLM "Software\JavaSoft\Java Runtime Environment" "CurrentVersion"
   IfErrors lbl_Java_Errors lbl_Java_Okay

   lbl_Java_Errors:
     StrCpy $R0 ''

   lbl_Java_Okay:
     Exch $R0
FunctionEnd


Function .onInit
  Call GetJavaVersion
  Pop $R0
  StrCmp $R0 "" lbl_JavaNotDetected lbl_JavaDetected
  lbl_JavaNotDetected:
    MessageBox MB_OK|MB_ICONSTOP "Please install Java ${JAVA_MAJOR}.${JAVA_MINOR} or greater (from http://java.com/) before installing this product."
    Quit
  lbl_JavaDetected:
    ; Check version is ${JAVA_MAJOR}.${JAVA_MINOR} or greater
    ; Check JAVA_MAJOR
    StrCpy $0 $R0 1 0
    IntCmp $0 ${JAVA_MAJOR} lbl_JavaDetected2 lbl_JavaLowVersion lbl_Java5Detected
  lbl_JavaDetected2:
    ; Check JAVA_MINOR
    StrCpy $0 $R0 1 2
    IntCmp $0 ${JAVA_MINOR} lbl_Java5Detected lbl_JavaLowVersion lbl_Java5Detected
  lbl_JavaLowVersion:
    MessageBox MB_OK|MB_ICONSTOP "You have Java $R0 installed.  Please install Java ${JAVA_MAJOR}.${JAVA_MINOR} or greater (from http://java.com/) before installing this product."
    Quit
  lbl_Java5Detected:
FunctionEnd

; Welcome page
!insertmacro MUI_PAGE_WELCOME
; License page
!insertmacro MUI_PAGE_LICENSE "lgpl.txt"
; Directory page
!insertmacro MUI_PAGE_DIRECTORY
; Instfiles page
!insertmacro MUI_PAGE_INSTFILES
; Finish page
!insertmacro MUI_PAGE_FINISH

; Uninstaller pages
!insertmacro MUI_UNPAGE_INSTFILES

; Language files
!insertmacro MUI_LANGUAGE "English"

; MUI end ------

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "worldclockapplication-setup-${PRODUCT_VERSION}.exe"
InstallDir "$PROGRAMFILES\worldclock"
ShowInstDetails show
ShowUnInstDetails show

Section "WorldClock" SEC01
  SetOutPath "$INSTDIR"
  SetOverwrite on

  File "worldclock.jar"
  File "icon32.ico"
  File "icon32.png"
  
SectionEnd



Section -AdditionalIcons
  CreateDirectory "$SMPROGRAMS\${PRODUCT_NAME}"
  CreateShortCut "$SMPROGRAMS\${PRODUCT_NAME}\World Clock.lnk" "$INSTDIR\worldclock.jar" "" "$INSTDIR\icon32.ico"
  CreateShortCut "$SMPROGRAMS\${PRODUCT_NAME}\Uninstall.lnk" "$INSTDIR\uninst.exe"
SectionEnd

Section -Post
  WriteUninstaller "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
SectionEnd


Function un.onUninstSuccess
  HideWindow
  MessageBox MB_ICONINFORMATION|MB_OK "$(^Name) was successfully removed from your computer."
FunctionEnd

Function un.onInit
  MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Are you sure you want to completely remove $(^Name) and all of its components?" IDYES +2
  Abort
FunctionEnd

Section Uninstall
  Delete "$INSTDIR\uninst.exe"

  Delete "$INSTDIR\worldclock.jar"
  Delete "$INSTDIR\icon32.ico"


  Delete "$SMPROGRAMS\${PRODUCT_NAME}\World Clock.lnk"
  Delete "$SMPROGRAMS\${PRODUCT_NAME}\Uninstall.lnk"

  RMDir "$SMPROGRAMS\${PRODUCT_NAME}"

  RMDir "$INSTDIR"

  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  SetAutoClose true
SectionEnd
