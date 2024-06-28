@echo off
setlocal

REM Standard paths
set SRC_PATH=src\main\algorithm
set TARGET_DIR=target
set OBJECT_DIR=%TARGET_DIR%\object
set ASSEMBLY_DIR=%TARGET_DIR%\assembly
set ROOT_DIR=src\main\algorithm

REM Compiler and flags
set CC=gcc
set CFLAGS=-Wall -g -Wextra -pedantic -std=c11 -I%ROOT_DIR%\include


REM Target executable
set TARGET=%TARGET_DIR%\algorithm.exe

REM Remove old .o and .s files if they exist
if exist "%OBJECT_DIR%\*.o" (
    echo Deleting old object files...
    del /q "%OBJECT_DIR%\*.o"
)
if exist "%ASSEMBLY_DIR%\*.s" (
    echo Deleting old assembly files...
    del /q "%ASSEMBLY_DIR%\*.s"
)

REM Create target directories if they don't exist
echo Creating target directories...
if not exist "%TARGET_DIR%" (
    echo Creating target directory: %TARGET_DIR%
    mkdir "%TARGET_DIR%"
    if errorlevel 1 (
        echo ERROR: Failed to create target directory.
        exit /b 1
    )
)
if not exist "%OBJECT_DIR%" (
    echo Creating object directory: %OBJECT_DIR%
    mkdir "%OBJECT_DIR%"
    if errorlevel 1 (
        echo ERROR: Failed to create object directory.
        exit /b 1
    )
)
if not exist "%ASSEMBLY_DIR%" (
    echo Creating assembly directory: %ASSEMBLY_DIR%
    mkdir "%ASSEMBLY_DIR%"
    if errorlevel 1 (
        echo ERROR: Failed to create assembly directory.
        exit /b 1
    )
)

REM Compile all source files in the source directory and subdirectories, excluding the tests folder
echo Compiling files...
setlocal enabledelayedexpansion
for /R "%ROOT_DIR%" %%f in (*.c) do (
    echo %%f | find "\src\main\algorithm\tests\" >nul
    if errorlevel 1 (
        echo Compiling %%f...
        set TEMPFILE=%OBJECT_DIR%\%%~nf.o
        echo !TEMPFILE!
        echo Command: %CC% %CFLAGS% -c "%%f" -o !TEMPFILE!
        %CC% %CFLAGS% -c "%%f" -o !TEMPFILE!
        if errorlevel 1 (
            echo ERROR: Compilation failed for %%f
            exit /b 1
        )
        echo Generating assembly file for %%f...
        set ASSEMBLY_FILE=%ASSEMBLY_DIR%\%%~nf.s
        echo Command: %CC% %CFLAGS% -S "%%f" -o !ASSEMBLY_FILE!
        %CC% %CFLAGS% -S "%%f" -o !ASSEMBLY_FILE!
        if errorlevel 1 (
            echo ERROR: Failed to generate assembly file for %%f
            exit /b 1
        )
    )
)

REM Link object files to produce the final executable
echo Linking object files...
%CC% %CFLAGS% -o "%TARGET%" "%OBJECT_DIR%\*.o"
if errorlevel 1 (
    echo ERROR: Linking failed.
    exit /b 1
)

REM Check if the build was successful
echo Build successful.


if "%1" neq "noExecution" (
	echo Starting the executable...
	echo.
	"%TARGET%"
)

endlocal
exit /b 0