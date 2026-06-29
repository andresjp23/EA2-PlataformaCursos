@echo off
title Plataforma de Cursos Online - Deteniendo sistema
cls
echo =====================================================================
echo  Deteniendo Plataforma de Cursos Online
echo =====================================================================
echo.
echo Deteniendo y eliminando contenedores...
cd /d %~dp0
docker compose down
if %errorlevel% neq 0 (
    echo ERROR: No se pudo detener el sistema.
    pause
    exit /b 1
)
echo.
echo =====================================================================
echo  Sistema detenido correctamente.
echo  Los datos de MySQL persisten en el volumen de Docker.
echo =====================================================================
pause
