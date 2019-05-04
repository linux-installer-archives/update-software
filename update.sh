#!/usr/bin/env bash

mvn compile \
    exec:java \
    -Dexec.mainClass="org.grumpyf0x48.liar.update.SoftwareUpdateRepository" \
    -Dexec.args="/home/pyfourmond/Sources/liar/.liar_software"
