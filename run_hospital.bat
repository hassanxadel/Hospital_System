@echo off
echo Starting Hospital Management System...
echo.
echo Make sure you have:
echo 1. Java JDK installed
echo 2. MySQL server running
echo 3. Database 'hospital_management' created
echo.
echo 💡 If database is not set up, run 'setup_database.bat' first!
echo.
pause

REM Set JAVA_HOME (adjust path as needed)
set JAVA_HOME=C:\Program Files\Java\jdk-8u301-windows-x64

REM Add Java to PATH
set PATH=%JAVA_HOME%\bin;%PATH%

REM Navigate to project directory
cd /d "%~dp0"

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK and set JAVA_HOME
    pause
    exit /b 1
)

REM Compile the project (if needed)
echo Compiling Java files...
javac -cp "store\SCHEDULAR.jar;." src\*.java

REM Run the application
echo Starting Hospital Management System...
java -cp "store\SCHEDULAR.jar;src" Entry

pause 