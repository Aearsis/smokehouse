#!/usr/bin/bash

exec java -cp "lib/sqlite-jdbc-3.15.1.jar:build" cz.eideo.smokehouse.server.Bootstrap $*
