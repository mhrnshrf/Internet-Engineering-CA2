rm -rf bin/*
javac -classpath coolserver.jar -sourcepath src -d bin src/*.java 

if [ $? -eq 0 ]; then
    cd bin
    jar cvfm ../CA2.jar ../manifest *.class
fi
