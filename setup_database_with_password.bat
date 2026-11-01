@echo off
echo 🏥 Hospital Management System - Database Setup (With Password Options)
echo =====================================================================
echo.
echo This script will help you set up the database with different password options.
echo.

:password_menu
echo Please choose your MySQL setup:
echo 1. MySQL with NO password (just press ENTER when prompted)
echo 2. MySQL with password (you'll enter it when prompted)
echo 3. I don't know my MySQL password (help me)
echo.
set /p choice="Enter your choice (1-3): "

if "%choice%"=="1" goto no_password
if "%choice%"=="2" goto with_password
if "%choice%"=="3" goto help_password
echo Invalid choice. Please try again.
goto password_menu

:no_password
echo.
echo Setting up with NO password...
echo When prompted for password, just press ENTER
echo.
mysql -u root -e "CREATE DATABASE IF NOT EXISTS hospital_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
if errorlevel 1 goto error_no_password
mysql -u root hospital_management < hp_mgmt_db.sql
if errorlevel 1 goto error_schema
goto success

:with_password
echo.
echo Setting up with password...
echo You'll be prompted to enter your MySQL root password twice.
echo.
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS hospital_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
if errorlevel 1 goto error_with_password
mysql -u root -p hospital_management < hp_mgmt_db.sql
if errorlevel 1 goto error_schema
goto success

:help_password
echo.
echo 🆘 MySQL Password Help:
echo.
echo Common default passwords to try:
echo • Empty (just press ENTER)
echo • root
echo • admin  
echo • password
echo • mysql
echo.
echo If none work, you may need to:
echo 1. Check your MySQL installation documentation
echo 2. Use MySQL Workbench if installed
echo 3. Reset MySQL password (advanced)
echo.
echo Would you like to try setup anyway? (y/n)
set /p try_anyway="Enter choice: "
if /i "%try_anyway%"=="y" goto password_menu
goto end

:error_no_password
echo ❌ Error: Could not connect with no password.
echo Try option 2 (with password) or check if MySQL is running.
pause
goto password_menu

:error_with_password
echo ❌ Error: Could not connect with password.
echo Please check:
echo • MySQL server is running
echo • Password is correct
echo • MySQL is in your system PATH
pause
goto password_menu

:error_schema
echo ❌ Error: Could not import schema.
echo Please ensure hp_mgmt_db.sql file exists in the current directory.
pause
goto end

:success
echo.
echo ✅ Database setup completed successfully!
echo.
echo 🎉 The database 'hospital_management' is ready to use!
echo You can now run the Hospital Management System.
echo.
pause
goto end

:end
echo.
echo Setup script finished.
pause 