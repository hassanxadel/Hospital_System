@echo off
echo 🏥 Hospital Management System - Database Setup
echo ===============================================
echo.
echo This script will help you set up the database for the Hospital Management System.
echo.
echo Prerequisites:
echo • MySQL Server should be running
echo • MySQL command line tools should be in PATH
echo.
pause

echo.
echo Setting up database...
echo.

REM Create the database
echo Creating database 'hospital_management'...
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS hospital_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

if errorlevel 1 (
    echo ❌ Error: Could not create database. Please check:
    echo • MySQL server is running
    echo • Root password is correct
    echo • MySQL is in your system PATH
    pause
    exit /b 1
)

echo ✅ Database created successfully!
echo.

echo Importing database schema...
mysql -u root -p hospital_management < hp_mgmt_db.sql

if errorlevel 1 (
    echo ❌ Error: Could not import schema.
    echo Please ensure hp_mgmt_db.sql file exists in the current directory.
    pause
    exit /b 1
)

echo ✅ Database schema imported successfully!
echo.

echo 🎉 Database setup completed!
echo.
echo You can now run the Hospital Management System.
echo The database 'hospital_management' is ready to use.
echo.
pause 