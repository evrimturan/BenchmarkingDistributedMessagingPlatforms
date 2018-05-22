#!/bin/bash

openssl rand -out "$1"/"producer.data-1KB" -base64 $(( 2**10 * 3/4 ))
openssl rand -out "$1"/"producer.data-128KB" -base64 $(( 2**17 * 3/4 ))
openssl rand -out "$1"/"producer.data-256KB" -base64 $(( 2**18 * 3/4 ))
openssl rand -out "$1"/"producer.data-512KB" -base64 $(( 2**19 * 3/4 ))
openssl rand -out "$1"/"producer.data-1MB" -base64 $(( 2**20 * 3/4 ))
openssl rand -out "$1"/"producer.data-2MB" -base64 $(( 2**21 * 3/4 ))
openssl rand -out "$1"/"producer.data-4MB" -base64 $(( 2**22 * 3/4 ))
#openssl rand -out "$1"/"producer.data-1GB" -base64 $(( 2**30 * 3/4 ))



