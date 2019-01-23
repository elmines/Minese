#!/bin/bash

mkdir -p dist
rm dist/*

cp Makefile dist/
cp scriptText.txt dist/
cp *.java dist/
cp test*.min dist/
cp README.md dist/
