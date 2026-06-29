@echo off
title Plataforma de Cursos Online - Sistema de Microservicios
cls
echo =====================================================================
echo  Plataforma de Cursos Online
echo  Iniciando sistema de microservicios con Docker
echo =====================================================================
echo.
echo [1/4] Verificando Docker...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker no esta instalado o no esta en ejecucion.
    echo Abre Docker Desktop y vuelve a intentarlo.
    pause
    exit /b 1
)
echo Docker detectado correctamente.
echo.
echo [2/4] Preparando contenedores...
cd /d %~dp0
echo.
echo [3/4] Levantando infraestructura (MySQL + Eureka + 10 microservicios + Gateway)...
echo Esto puede tardar varios minutos la primera vez...
docker compose up -d
if %errorlevel% neq 0 (
    echo ERROR: No se pudo iniciar el sistema.
    pause
    exit /b 1
)
echo.
echo [4/4] Verificando estado...
docker compose ps
echo.
echo =====================================================================
echo  ^¡Sistema iniciado con exito!
echo.
echo  URLs de acceso:
echo  - API Gateway:     http://localhost:8080
echo  - Eureka Server:  http://localhost:8761
echo.
echo  Documentacion Swagger (cuando los servicios esten listos):
echo  - Auth:           http://localhost:8081/doc/swagger-ui.html
echo  - Users:          http://localhost:8082/doc/swagger-ui.html
echo  - Categories:     http://localhost:8083/doc/swagger-ui.html
echo  - Lessons:        http://localhost:8084/doc/swagger-ui.html
echo  - Courses:        http://localhost:8085/doc/swagger-ui.html
echo  - Enrollments:    http://localhost:8086/doc/swagger-ui.html
echo  - Progress:       http://localhost:8087/doc/swagger-ui.html
echo  - Evaluations:    http://localhost:8088/doc/swagger-ui.html
echo  - Certificates:   http://localhost:8089/doc/swagger-ui.html
echo  - Grades:         http://localhost:8090/doc/swagger-ui.html
echo.
echo  Para ver los logs en vivo: docker compose logs -f
echo  Para detener el sistema:   docker compose down
echo =====================================================================
pause
