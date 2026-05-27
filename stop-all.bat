@echo off
setlocal EnableDelayedExpansion
title Library System - Stop All
color 0C

echo.
echo  ========================================
echo       Library System - Stop All
echo  ========================================
echo.

set "stopped_count=0"

REM ========== Stop Backend ==========
echo [1/3] Stopping backend service ^(Port: 8080^)...
set "found_backend=0"
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080 " ^| findstr "LISTENING" 2^>nul') do (
    set "found_backend=1"
    taskkill /PID %%a /F >nul 2>&1
    if not errorlevel 1 (
        echo      [OK] Backend stopped ^(PID: %%a^)
        set /a stopped_count+=1
    ) else (
        echo      [WARN] Cannot stop PID: %%a
    )
)
if "!found_backend!"=="0" (
    echo      [SKIP] Backend not running
)
echo.

REM ========== Stop User Portal ==========
echo [2/3] Stopping user portal ^(Port: 5173^)...
set "found_user=0"
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173 " ^| findstr "LISTENING" 2^>nul') do (
    set "found_user=1"
    taskkill /PID %%a /F >nul 2>&1
    if not errorlevel 1 (
        echo      [OK] User portal stopped ^(PID: %%a^)
        set /a stopped_count+=1
    ) else (
        echo      [WARN] Cannot stop PID: %%a
    )
)
if "!found_user!"=="0" (
    echo      [SKIP] User portal not running
)
echo.

REM ========== Stop Admin Portal ==========
echo [3/3] Stopping admin portal ^(Port: 5174^)...
set "found_admin=0"
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5174 " ^| findstr "LISTENING" 2^>nul') do (
    set "found_admin=1"
    taskkill /PID %%a /F >nul 2>&1
    if not errorlevel 1 (
        echo      [OK] Admin portal stopped ^(PID: %%a^)
        set /a stopped_count+=1
    ) else (
        echo      [WARN] Cannot stop PID: %%a
    )
)
if "!found_admin!"=="0" (
    echo      [SKIP] Admin portal not running
)
echo.

REM ========== Summary ==========
color 0A
echo  ========================================
if !stopped_count! gtr 0 (
    echo       Stopped !stopped_count! service^(s^)
) else (
    echo       No running services found
)
echo  ========================================
echo.

endlocal
pause
