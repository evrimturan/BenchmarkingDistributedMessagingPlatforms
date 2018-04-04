#!/usr/bin/env bash

filename="producer.data-"

for index in $(eval echo "{0..$1}")
do
    openssl rand -out ../$3/$filename$index -base64 $(( $2 * 3/4 ))
done




