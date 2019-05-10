#!/usr/bin/env bash

mvn -DskipTests -Dmaven.test.skip=true clean package

time mvn exec:java \
    -Dexec.mainClass="org.grumpyf0x48.liar.update.SoftwareUpdateRepository" \
    -Dexec.args="/home/pyfourmond/Sources/liar/.liar_software"
