#!/bin/bash

git pull >> /dev/null 2>&1

clear

mvn clean compile assembly:single

cp target/SeniorDesign-1.0-jar-with-dependencies.jar $( pwd )

mv SeniorDesign-1.0-jar-with-dependencies.jar SeniorDesign-1.0.jar

java -jar SeniorDesign-1.0.jar "test.config"