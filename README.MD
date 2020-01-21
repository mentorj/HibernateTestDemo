# Hibernate testing 

## Introduction
This repository contains stuff to improve Hibernate testing....
Basic concepts are present, use Hibernate stats to pick up information in assertions to ensure that everything is ok
Getting the statistics is a little bit tricky because of JPA usage (so getting the Session requires to unwrap the Session class)
Custom appender is now useless,  using direct statistics is much more clean..

## Basic ideas
Use this code as a starting point, import your entities to be testing, create some test equivalent to the one provided
and check results....
Beware while choosing business key for equals
Warning while being consistent between equals & hashcode (cf Product.java)

Test cases may use:
- special slf4j and log4j features to check traces generated and grep patterns (and count them)
- Hibernate statistics (to count queries...)

## Using this code
Import your entities and use them rather than the Product class provided...
Adds annotation for caching
Configure the correct cache sizing...

## Improving the code
Adds much more complex usage with relationships and check against the N+1 queries...
Once again Hibernate statistics would be helpful to ensure you avoid the N+1 queries...
==> adds one more test case
## Conclusions
reach me at jerome@javaxpert.com
