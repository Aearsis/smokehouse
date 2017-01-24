#!/usr/bin/bash

exec java -cp target/classes:lib/sqlite-jdbc-3.15.1.jar cz.eideo.smokehouse.client.Bootstrap $*
