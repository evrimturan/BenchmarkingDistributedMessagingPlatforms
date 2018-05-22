#!/bin/bash

clear

mvn clean compile assembly:single

cp target/SeniorDesign-1.0-jar-with-dependencies.jar $( pwd )

mv SeniorDesign-1.0-jar-with-dependencies.jar SeniorDesign-1.0.jar

for file in "$1"tests/"$2"*
do
    echo "Doing for : $file"
    java -jar SeniorDesign-1.0.jar activemqtests/"$file"
done