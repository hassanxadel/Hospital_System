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

REM Navigate to project directory
cd /d "%~dp0"

REM Check if Java is available on PATH
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install a JDK and add it to PATH
    pause
    exit /b 1
)

REM Compile the project (if needed)
echo Compiling Java files...
if not exist build\classes mkdir build\classes
javac -encoding UTF-8 -cp "dist\lib\AbsoluteLayout.jar;lib\mysql-connector-j-8.0.33.jar" -d build\classes src\*.java
if errorlevel 1 (
    echo ERROR: Compilation failed.
    pause
    exit /b 1
)

REM Run the application (login screen is the real entry point)
echo Starting Hospital Management System...
java -cp "build\classes;dist\lib\AbsoluteLayout.jar;lib\mysql-connector-j-8.0.33.jar;src" login

pause 