if "%~1"=="" (
   echo please input version number
    exit /b 1
)


call mvn clean package

if errorlevel 1 (
    echo Maven build failed.
)


REM Set the version from the command-line parameter
set version=%~1

REM Build Docker image
echo Building Docker image with tag mvc-v%version%...
docker build -t mvc-test-v%version% .

REM Run Docker container
echo Running Docker container with tag mvc-v%version% on port 8080...
docker run -p 8080:8080 mvc-test-v%version%

endlocal
