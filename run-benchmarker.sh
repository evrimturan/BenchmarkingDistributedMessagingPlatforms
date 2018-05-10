#!/bin/bash

clear

mvn clean compile assembly:single

cp target/SeniorDesign-1.0-jar-with-dependencies.jar $( pwd )

mv SeniorDesign-1.0-jar-with-dependencies.jar SeniorDesign-1.0.jar

for file in activemqtests/*
do
    echo "Doing for : $file"
    java -jar SeniorDesign-1.0.jar activemqtests/"$file"
done

for file in rabbitmqtests/*
do
    echo "Doing for : $file"
    java -jar SeniorDesign-1.0.jar rabbitmqtests/"$file"
done

for file in kafkatests/*
do
    echo "Doing for: $file"
    java -jar SeniorDesign-1.0.jar kafkatests/"$file"
done
