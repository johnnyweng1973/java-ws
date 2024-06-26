if "%~1"=="" (
   echo please input version number
    exit /b 1
)

REM Set the version from the command-line parameter
set version=%~1

call mvn clean package

REM Build Docker image
echo Building Docker image with tag mvc-v%version%...
docker build -t mvc-v%version% .

REM Run Docker container
echo Running Docker container with tag mvc-v%version% on port 8080...
docker run -p 8080:8080 mvc-v%version%

endlocal
