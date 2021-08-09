# AWS  AND AZURE POC Using Dagger2


## Scope
---

This app is a poc used to demonstrate the Push Notification capabilities of Amazaon's Simple Notification Services and Azure Notification Hub

## Architecture 


This app uses a **MVVM** architecture with the local data base holding the topics in case of AWS . The data is synced with the cloud providers as per user's choice .
The Azure version currently  does not support topics 
The app has code for both aws and Azure connectivity which is separated by its respective build flavours .

It also uses **Daggger2** for dependency injection 

## Build 

To build this app create a gradle.properties file in the Gradle home of your build machine .
Add add the AWS and Azure connectivity keys 
For example 
AWS_PROD_ACCESS_KEY = "sone value etc "
See the app build.gradle file for key names 

To build run ./gradlew buildAwsDev
or ./gradlew buildAzureDev

