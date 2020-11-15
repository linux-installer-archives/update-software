LIAR_SOFTWARE=${LIAR_REPOSITORY}/liar-software

# This target does not run tests
build_native: clean
	JAVA_HOME=${GRAALVM_HOME} mvn -Dmaven.test.skip=true -DskipTests -Pnative install

# This target runs tests
build: clean
	mvn install

test: build

# This target does not depend on install_native because it is very long to build
update_software_native: build_native
	./target/SoftwareUpdateRepository.native \
		-Djava.library.path=${JAVA_HOME}/jre/lib/amd64 \
		-Djavax.net.ssl.trustStore=${JAVA_HOME}/jre/lib/security/cacerts \
		${LIAR_SOFTWARE} \
		${LIAR_PERIODICITY}

update_software: build
	mvn exec:java \
		-Dexec.mainClass=org.grumpyf0x48.liar.update.SoftwareUpdateRepository \
		-Dexec.args="${LIAR_SOFTWARE} ${LIAR_PERIODICITY}"

clean:
	mvn clean

ifndef LIAR_REPOSITORY
$(error Please set LIAR_REPOSITORY !)
endif

ifndef LIAR_PERIODICITY
LIAR_PERIODICITY=MONTHLY
endif
