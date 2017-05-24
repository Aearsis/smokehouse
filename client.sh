#!/usr/bin/bash

exec java -cp "build:lib/sqlite-jdbc-3.15.1.jar" cz.eideo.smokehouse.client.Bootstrap $*
