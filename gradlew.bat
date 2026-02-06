@ECHO OFF
SET DIR=%~dp0
SET JAVA_EXE=java
"%JAVA_EXE%" -jar "%DIR%gradle\wrapper\gradle-wrapper.jar" %*
