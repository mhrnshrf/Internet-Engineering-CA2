if [ $? -eq 0 ]; then
    echo "Starting Server"
    java -jar CA2.jar -cp coolserver.jar
fi
