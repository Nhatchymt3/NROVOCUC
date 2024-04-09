@ECHO OFF
java -server -jar -Dfile.encoding=UTF-8 dist/Nro.jar > console.txt 2> error.txt
PAUSE