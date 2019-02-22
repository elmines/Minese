#!/bin/bash

mkdir -p dist/
rm dist/*

cp Makefile dist/
cp *.min dist/
cp *.java dist/
cp README dist/
cp iAVL dist/
cp commands dist/
cp scriptText.txt dist/

cd dist/
rm TestEnv.java
rm TestRec.java
rm Scanner.java

cd ..
