# CICS-Camel-Component FUSE 6.1 -> It's mandatory to do a real test in a CTG server environment!!!!!  
=======================

This is a small proof of concept about Camel CICS component to connect with mainframe through CICS Gateway Transaction client.
Right now, the response is a byte[] variable, it should be transformed to other structure.

## Features
 * ECI (External call interface) calling a CICS program in a CICS server.
 * Endpoint 
     syntax = "cics:server/program?options[]"
     example with all options->  cics:serverName1/program1?sslKeyring=sslKeyring1&sslPassword=sslPassword1&userId=userId1&password=password1&commAreaSize=0&port=100

## Requirements
 * CICS Transaction Gateway version >= 9.1 must be installed.
 * Fuse 6.1 access to the Fuse maven repostory
  

## Instructions 
### 1- Download CICS Transaction Gateway client
    Version CICS Transaction Gateway client 9.1 or higher
### 2- Obtain driver OSGI bundle 
Extract the CICS® TG Client API bundle, com.ibm.ctg.client-1.0.0.jar from the CICS TG SDK package cicstgsdk/api/java/runtime to a directory on the local file system IBM drivers
### 3- Install driver in local maven repository 
       mvn install:install-file -Dfile=com.ibm.ctg.client-1.0.0.jar -DgroupId=com.ibm.ctg -DartifactId=client -Dversion=1.0.0 -Dpackaging=jar
### 4- Build this project use
       mvn install
