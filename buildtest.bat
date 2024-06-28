@echo off
setlocal

if "%1"=="all" (
	echo compiling main...
	call build.bat noExecution
	if errorLevel 1 (
		echo The build process failed. Execution of the program stopped.
		exit /b 1
	)
	echo.
)

REM Root directory
set ROOT_DIR=src\main\algorithm\tests

REM Paths
set TARGET_DIR=target
set SRC_INCLUDE_DIR=src\main\algorithm\include
set OBJECT_DIR=%TARGET_DIR%\object
set TEST_OBJECT_DIR=%TARGET_DIR%\tests
set TEST_INCLUDE_DIR=%ROOT_DIR%\include
set TEST_EXE=%TARGET_DIR%\tests.exe
set SHARED_LIB=%TARGET_DIR%\tests\libmockexit.dll

REM Compiler and flags
set CC=gcc
set CFLAGS=-Wall -g -Wextra -pedantic -std=c11 
set LDFLAGS=

REM Remove old .o files if they exist
if exist "%TEST_OBJECT_DIR%\*.o" (
    echo Deleting old test object files...
    del /q "%TEST_OBJECT_DIR%\*.o"
)

if not exist "%TEST_OBJECT_DIR%" (
    echo Creating tests object directory: %TEST_OBJECT_DIR%
    mkdir "%TEST_OBJECT_DIR%"
    if errorlevel 1 (
        echo ERROR: Failed to create tests object directory.
        exit /b 1
    )
)

REM Compile test files
echo Compiling test files...
setlocal enabledelayedexpansion

for /R %ROOT_DIR% %%f in (*.c) do (
    echo Compiling %%f...
    set TEMPFILE=%TEST_OBJECT_DIR%\%%~nf.o
    if not "%%~nxf"=="mockExit.c" (
        %CC% %CFLAGS% -I%TEST_INCLUDE_DIR% -I%SRC_INCLUDE_DIR% -c "%%f" -o "!TEMPFILE!"
        if errorlevel 1 (
            echo ERROR: Compilation failed for %%f
            exit /b 1
        )
    )
)

REM Compile the static library
echo Compiling static library...
%CC% %CFLAGS% -c %ROOT_DIR%\mockExit.c -o %TEST_OBJECT_DIR%\libmockexit.o
ar rcs %TEST_OBJECT_DIR%\libmockexit.a %TEST_OBJECT_DIR%\libmockexit.o
if errorlevel 1 (
    echo ERROR: Compilation of the static library failed.
    exit /b 1
)

REM Link object files to produce the final test executable
echo Linking object files...
set LINK_FILES=
for %%f in (%OBJECT_DIR%\*.o) do (
    if not "%%~nxf"=="main.o" if not "%%~nxf"=="errors.o" (
        set LINK_FILES=!LINK_FILES! "%%f"
    )
)
for %%f in (%TEST_OBJECT_DIR%\*.o) do (
    if not "%%~nxf"=="libmockexit.o" (
        set LINK_FILES=!LINK_FILES! "%%f"
    )
)
set LINK_FILES=!LINK_FILES! "%TARGET_DIR%\tests\libmockexit.a"
%CC% %CFLAGS% -o "%TEST_EXE%" !LINK_FILES!
if errorlevel 1 (
    echo ERROR: Linking failed.
    exit /b 1
)

REM Check if the build was successful
echo Build successful.
echo Test executable created at: %TEST_EXE%
echo Static library created at: %TEST_OBJECT_DIR%\libmockexit.a

REM Run the test executable
if "%2"=="gdb" (
	gdb %TEST_EXE%
) else (
	echo Running tests...
	"%TEST_EXE%"
)


REM Check if the execution was successful
if errorlevel 1 (
    echo ERROR: Test execution failed.
    exit /b 1
)

endlocal
