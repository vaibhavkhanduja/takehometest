# Druid Coding challenge - analyzing user sessions

## What we are doing?
The aim of the work to solve for coding challenge presented as part of interview process at Imply. The details of the coding challenge can be obtained here
https://gist.github.com/gianm/bfa7e4abebfb0da6346b

## Solution Proposed
The coding challenge presents with a large file with the schema
<timestamp>:<Path>:<userID>
 
The aim is write out a program that scan through the log file of similar schema, of any size and presents output of paths along with their userid which have been  alteast visited N times by its user.

### Approach
The challenge is attempted in java as a Maven project 

In this problem statement, the logs are created with a specific schema 

Path:TimeStamp:UserId

The challenge is to consolidate these logs and summarize by printing user ids of atleast N distinct path.
 
### Assumption
### Design
The choice of programming is java. The approach taken here is based on the assumption 
### How to test
### Pending Work 

A short description of how to build and run your program
The general approach you're taking, and why you chose it
Any alternate approaches you considered, and why you didn't choose them
A brief analysis of the resource needs and performance of your chosen approach
Any assumptions you made about the properties of the dataset
Any improvements you could make in the future
