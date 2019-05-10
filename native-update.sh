#!/usr/bin/env bash

# 1. Installer GraalVM
# 2. sudo apt-get install zlib1g-dev

export GRAALVM_HOME=/home/pyfourmond/Programs/graalvm/graalvm-ce-1.0.0-rc15
export JAVA_HOME=${GRAALVM_HOME}

mvn -Pnative -DskipTests -Dmaven.test.skip=true clean package

time ./target/SoftwareUpdateRepository.native \
    -Djava.library.path=${GRAALVM_HOME}/jre/lib/amd64 \
    -Djavax.net.ssl.trustStore=${GRAALVM_HOME}/jre/lib/security/cacerts \
    /home/pyfourmond/Sources/liar/.liar_software
