@echo off
echo Compilation en cours...
javac -d bin --module-path "D:\Program Files\openjfx-21.0.9_windows-x64_bin-sdk\javafx-sdk-21.0.9\lib" --add-modules javafx.controls -cp "src" src/com/formation/mvc/view/MainGUI.java

if %errorlevel% equ 0 (
    echo Lancement de l'application...
    java --module-path "D:\Program Files\openjfx-21.0.9_windows-x64_bin-sdk\javafx-sdk-21.0.9\lib" --add-modules javafx.controls -cp "bin" com.formation.mvc.view.MainGUI
) else (
    echo Erreur de compilation!
)

pause