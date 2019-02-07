#!/bin/bash

mkdir -p dist/
rm dist/*

cp Makefile dist/
cp *.java dist/
cp envAssign/* dist/
