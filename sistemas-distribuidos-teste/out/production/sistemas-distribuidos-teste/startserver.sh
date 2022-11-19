#!/bin/bash
echo "Socket RMI Messenger Server"
$(rmiregistry 9915) &
$(javac *.java)
$(java MessengerServer)