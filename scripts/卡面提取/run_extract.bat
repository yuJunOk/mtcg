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

REM 切换到项目根目录（脚本在 scripts/卡面提取/ 下）
cd /d "%~dp0..\.."

REM 默认处理 assets/card/raw 目录下的原始截图
set "CARD_DIR=assets\card\raw"
set "OUTPUT_DIR=assets\card\extracted"

if not exist "%CARD_DIR%" (
    echo [ERROR] Raw screenshot directory not found: %CARD_DIR%
    echo         Please put your screenshots in "assets\card\raw\" first.
    pause
    exit /b 1
)

"%PYTHON%" "scripts\卡面提取\extract_card.py" "%CARD_DIR%" -o "%OUTPUT_DIR%"
echo.
echo Done! Check %OUTPUT_DIR%\
pause
