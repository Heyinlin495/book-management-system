@echo off
setlocal EnableDelayedExpansion
title Library System - Start All
color 0A

echo.
echo  ========================================
echo       Library System - Start All
echo  ========================================
echo.

REM ========== Environment Check ==========
echo [1/5] Checking environment...

REM Check Java
where java >nul 2>&1
if errorlevel 1 (
    color 0C
    echo      [ERROR] Java not found, please install JDK 21!
    pause
    exit /b 1
)
for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    echo      [OK] Java: %%~v
    goto :java_ok
)
:java_ok

REM Check Node.js
where node >nul 2>&1
if errorlevel 1 (
    color 0C
    echo      [ERROR] Node.js not found, please install!
    pause
    exit /b 1
)
for /f "tokens=*" %%v in ('node -v') do echo      [OK] Node.js: %%v
echo.

REM ========== MySQL Check ==========
echo [2/5] Checking MySQL database...
netstat -ano | findstr ":3306" | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    color 0E
    echo      [WARN] MySQL port 3306 not listening!
    echo      Please start MySQL first, press any key to continue...
    pause >nul
    color 0A
) else (
    echo      [OK] MySQL is running
)
echo.

REM ========== Check Port Usage ==========
echo [3/5] Checking port availability...
set "port_error=0"

netstat -ano | findstr ":8080 " | findstr "LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo      [WARN] Port 8080 in use, trying to close...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080 " ^| findstr "LISTENING"') do (
        taskkill /PID %%a /F >nul 2>&1
    )
    timeout /t 2 /nobreak >nul
)
echo      [OK] Port 8080 available

netstat -ano | findstr ":5173 " | findstr "LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo      [WARN] Port 5173 in use, trying to close...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173 " ^| findstr "LISTENING"') do (
        taskkill /PID %%a /F >nul 2>&1
    )
    timeout /t 1 /nobreak >nul
)
echo      [OK] Port 5173 available

netstat -ano | findstr ":5174 " | findstr "LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo      [WARN] Port 5174 in use, trying to close...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5174 " ^| findstr "LISTENING"') do (
        taskkill /PID %%a /F >nul 2>&1
    )
    timeout /t 1 /nobreak >nul
)
echo      [OK] Port 5174 available
echo.

REM ========== Start Backend ==========
echo [4/5] Starting backend service...
cd /d "%~dp0"
start "Backend-8080" cmd /c "cd /d "%~dp0" && mvnw.cmd spring-boot:run"
echo      Backend is starting, waiting for service ready...

REM Wait for backend to start (max 60 seconds)
set /a count=0
:wait_backend
timeout /t 3 /nobreak >nul
set /a count+=3
netstat -ano | findstr ":8080 " | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    if !count! lss 60 (
        echo      Waiting... [!count!s]
        goto :wait_backend
    ) else (
        color 0E
        echo      [WARN] Backend startup timeout, please check backend window
        color 0A
    )
) else (
    echo      [OK] Backend started ^(Port: 8080^)
)
echo.

REM ========== Start Frontend ==========
echo [5/5] Starting frontend services...
cd /d "%~dp0frontend"

REM Check dependencies
if not exist "node_modules" (
    echo      Installing frontend dependencies...
    call npm install --silent
    if errorlevel 1 (
        color 0C
        echo      [ERROR] Dependency installation failed!
        pause
        exit /b 1
    )
    echo      [OK] Dependencies installed
) else (
    echo      [OK] Dependencies exist
)

REM Start user portal
echo      Starting user portal ^(Port: 5173^)...
start "User-5173" cmd /c "cd /d "%~dp0frontend" && npm run dev:user"
timeout /t 3 /nobreak >nul

REM Start admin portal
echo      Starting admin portal ^(Port: 5174^)...
start "Admin-5174" cmd /c "cd /d "%~dp0frontend" && npm run dev:admin"
timeout /t 3 /nobreak >nul

REM Verify frontend startup
netstat -ano | findstr ":5173 " | findstr "LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo      [OK] User portal started
) else (
    echo      [WARN] User portal starting...
)

netstat -ano | findstr ":5174 " | findstr "LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo      [OK] Admin portal started
) else (
    echo      [WARN] Admin portal starting...
)
echo.

REM ========== Complete ==========
echo  ========================================
echo          All services started!
echo  ========================================
echo.
echo  Service URLs:
echo    Backend API:   http://localhost:8080
echo    User Portal:   http://localhost:5173
echo    Admin Portal:  http://localhost:5174
echo.
echo  Tips:
echo    - Close window to stop service
echo    - Run stop-all.bat to stop all services
echo.

REM Ask to open browser
echo.
set /p open_browser="Open browser to user portal? (Y/N): "
if /i "%open_browser%"=="Y" (
    start http://localhost:5173
)

endlocal
pause
