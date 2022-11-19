@echo "Socket RMI Messenger Server"
start rmiregistry 9915
javac *.java
java MessengerServer
pause