@echo off
REM Compile et lance les tests unitaires (JUnit 5).
REM Le lanceur JUnit est telecharge automatiquement dans lib\ au premier lancement.
REM Prerequis : JDK 21 et JAVAFX_HOME (voir run.bat).

set JUNIT_VERSION=1.10.2
set JUNIT_JAR=lib\junit-platform-console-standalone-%JUNIT_VERSION%.jar

if not exist "%JUNIT_JAR%" (
    echo Telechargement de JUnit %JUNIT_VERSION%...
    if not exist lib mkdir lib
    curl -L -o "%JUNIT_JAR%" https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/%JUNIT_VERSION%/junit-platform-console-standalone-%JUNIT_VERSION%.jar
)

if "%JAVAFX_HOME%"=="" (
    echo Erreur : la variable JAVAFX_HOME n'est pas definie.
    pause
    exit /b 1
)

echo Compilation...
if exist bin-test rmdir /s /q bin-test
mkdir bin-test
dir /s /b src\*.java test\*.java > sources.txt
javac -encoding UTF-8 -d bin-test -cp "%JUNIT_JAR%" --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls @sources.txt
set RESULTAT=%errorlevel%
del sources.txt
if not %RESULTAT% equ 0 (
    echo Erreur de compilation !
    pause
    exit /b 1
)

java --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls -jar "%JUNIT_JAR%" -cp bin-test --scan-classpath
pause
