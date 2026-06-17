@echo off
chcp 65001 > nul
cd /d "%~dp0"
echo === Compilando RPG por Turnos ===

set JAVAC=C:\Program Files\openJdk-25\bin\javac.exe
set JAVA=C:\Program Files\openJdk-25\bin\java.exe

if not exist bin mkdir bin

:: Recopilar todos los .java con rutas entre comillas (soporta espacios en el path)
if exist sources_temp.txt del sources_temp.txt
for /r src %%f in (*.java) do (
    set "RUTA=%%f"
    call :agregarRuta
)
goto :compilar

:agregarRuta
set "RUTA_FWD=%RUTA:\=/%"
echo "%RUTA_FWD%">> sources_temp.txt
goto :eof

:compilar
"%JAVAC%" -encoding UTF-8 -sourcepath src -d bin @sources_temp.txt

del sources_temp.txt

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] La compilacion fallo. Revisa los errores anteriores.
    pause
    exit /b 1
)

echo.
echo [OK] Compilacion exitosa! ^(73 clases^)
echo === Iniciando el juego ===
"%JAVA%" -cp bin Main
pause
