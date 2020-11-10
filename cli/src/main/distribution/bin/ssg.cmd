@echo off

if "%SSG_HOME%" == "" (
  echo SSG_HOME variable must be set
  exit /b 1
)

"java" ^
  "-Dlogback.configurationFile=%SSG_HOME%\conf\logback.xml" ^
  %JAVA_OPTS% ^
  -classpath "%SSG_HOME%\lib\*;%SSG_HOME%\lib\ext\*" ^
  --add-opens java.base/java.time.format=ALL-UNNAMED ^
  "com.github.gclaussn.ssg.cli.Cli" ^
  %*
