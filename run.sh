#!/bin/sh

Client=Client
PROJECT_PATH=/home/odroid/Downloads/flycapture.client
JAR_PATH=$PROJECT_PATH/lib
BIN_PATH=$PROJECT_PATH/bin/$Client

#../FlyCapture2Test/FlyCapture2Test
java -cp $BIN_PATH:/$JAR_PATH/commons-logging-1.2.jar:$JAR_PATH/gson-2.3.1.jar  Client 
