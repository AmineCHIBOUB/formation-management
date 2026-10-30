@echo off
REM Lance l'application JavaFX.
REM Prerequis : JDK 21 et le SDK JavaFX 21 (https://gluonhq.com/products/javafx/)
REM Definir JAVAFX_HOME vers le dossier du SDK, par exemple :
REM   set JAVAFX_HOME=C:\javafx-sdk-21

if "%JAVAFX_HOME%"=="" (
    echo Erreur : la variable JAVAFX_HOME n'est pas definie.
    echo Exemple : set JAVAFX_HOME=C:\javafx-sdk-21
    pause
    exit /b 1
)

echo Compilation en cours...
if not exist bin mkdir bin
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d bin --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls @sources.txt
set RESULTAT=%errorlevel%
del sources.txt

if %RESULTAT% equ 0 (
    echo Lancement de l'application...
    java --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls -cp bin com.formation.mvc.view.MainGUI
) else (
    echo Erreur de compilation !
)

pause
