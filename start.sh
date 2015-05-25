#!/bin/bash

set -x;
supervisor --watch js/server.js,$1,js/scripts,js/bower.json,js/package.json -- js/server.js $1 > log/node.out 2>&1 &
export MAVEN_OPTS="-ea -da:com.rapidminer..."
mvn compile > log/compile.out 2>&1
mvn exec:java -Dexec.args="$1" > log/worker.out 2>&1 &
