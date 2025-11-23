@echo off
chcp 65001 > nul
echo ========================================
echo 🧬 MUTANT DETECTION API
echo ========================================
echo.
echo Opciones:
echo [1] Ejecutar aplicación
echo [2] Compilar proyecto
echo [3] Ejecutar tests
echo [4] Ver reporte de cobertura
echo [5] Limpiar build
echo [6] Compilar Docker
echo [7] Ejecutar Docker
echo [8] Ver logs Docker
echo [9] Probar algoritmo manualmente
echo [0] Salir
echo.
set /p option="Selecciona una opción: "

if "%option%"=="1" goto run
if "%option%"=="2" goto build
if "%option%"=="3" goto test
if "%option%"=="4" goto coverage
if "%option%"=="5" goto clean
if "%option%"=="6" goto docker_build
if "%option%"=="7" goto docker_run
if "%option%"=="8" goto docker_logs
if "%option%"=="9" goto test_algorithm
if "%option%"=="0" goto end
goto menu

:run
echo.
echo ========================================
echo Ejecutando aplicación...
echo ========================================
echo.
echo La aplicación estará disponible en:
echo - API: http://localhost:8080
echo - Swagger: http://localhost:8080/swagger-ui.html
echo - H2 Console: http://localhost:8080/h2-console
echo.
gradlew.bat bootRun
goto end

:build
echo.
echo ========================================
echo Compilando proyecto...
echo ========================================
echo.
gradlew.bat clean build -x test
echo.
echo ✅ Compilación completada
pause
goto menu

:test
echo.
echo ========================================
echo Ejecutando tests...
echo ========================================
echo.
gradlew.bat test
pause
goto menu

:coverage
echo.
echo ========================================
echo Generando reporte de cobertura...
echo ========================================
echo.
gradlew.bat test jacocoTestReport
start build\reports\jacoco\test\html\index.html
goto menu

:clean
echo.
echo ========================================
echo Limpiando build...
echo ========================================
echo.
gradlew.bat clean
echo.
echo ✅ Limpieza completada
pause
goto menu

:docker_build
echo.
echo ========================================
echo Construyendo imagen Docker...
echo ========================================
echo.
docker build -t mutant-detector:1.0 .
echo.
echo ✅ Imagen creada
pause
goto menu

:docker_run
echo.
echo ========================================
echo Ejecutando contenedor Docker...
echo ========================================
echo.
docker run -d -p 8080:8080 --name mutant-app mutant-detector:1.0
echo.
echo ✅ Contenedor iniciado
echo Accede a: http://localhost:8080/swagger-ui.html
pause
goto menu

:docker_logs
echo.
echo ========================================
echo Logs del contenedor Docker
echo ========================================
echo.
docker logs -f mutant-app
goto menu

:test_algorithm
echo.
echo ========================================
echo Probando algoritmo manualmente...
echo ========================================
echo.
gradlew.bat compileJava
java -cp build\classes\java\main org.example.TestMutantDetector
pause
goto menu

:end
echo.
echo Hasta luego!
exit

