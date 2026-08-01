@echo off
chcp 65001 >nul 2>&1
echo ============================================
echo   MTCG Card Face Extractor
echo ============================================
echo.

echo [1/3] Set PowerShell ExecutionPolicy...
powershell -ExecutionPolicy Bypass -Command "Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned -Force" 2>nul
if %errorlevel% == 0 (
    echo       Done
) else (
    echo       [WARN] Failed, will try direct run...
)
echo.

echo [2/3] Check Python...
set "PYTHON=D:\ProgramApp\Python\python-3.13.14\python.exe"
if not exist "%PYTHON%" (
    echo [ERROR] Python not found at: %PYTHON%
    pause
    exit /b 1
)
"%PYTHON%" --version
echo.

echo [3/3] Check Pillow...
"%PYTHON%" -c "from PIL import Image; print('Pillow OK')" 2>nul
if errorlevel 1 (
    echo       Installing Pillow...
    "%PYTHON%" -m pip install Pillow
)
echo.

echo ============================================
echo   Start extracting card faces...
echo ============================================
cd /d "%~dp0"
"%PYTHON%" "scripts\卡面提取\extract_card.py" "assets\card\raw" -o "assets\card\extracted"
echo.
echo Done! Check assets\card\extracted\
pause
