#!/bin/sh

Client=Client
PROJECT_PATH=/home/odroid/Downloads/flycapture.client
JAR_PATH=$PROJECT_PATH/lib
BIN_PATH=$PROJECT_PATH/bin
SRC_PATH=$PROJECT_PATH/src/

rm -f $SRC_PATH/sources
find $SRC_PATH/com -iname *.java > $SRC_PATH/sources.list

rm -rf $BIN_PATH/$Client
mkdir $BIN_PATH/$Client

javac -d $BIN_PATH/$Client -classpath $JAR_PATH/commons-logging-1.2.jar:$JAR_PATH/gson-2.3.1.jar @$SRC_PATH/sources.list
