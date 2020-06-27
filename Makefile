LIAR_SOFTWARE=${LIAR_REPOSITORY}/.liar_software

update: install
	mvn exec:java \
		-Dexec.mainClass=org.grumpyf0x48.liar.update.SoftwareUpdateRepository \
		-Dexec.args=${LIAR_SOFTWARE}

# This target does not depend on install_native because it is very long to build
update_native:
	./target/SoftwareUpdateRepository.native \
		-Djava.library.path=${JAVA_HOME}/jre/lib/amd64 \
		-Djavax.net.ssl.trustStore=${JAVA_HOME}/jre/lib/security/cacerts \
		${LIAR_SOFTWARE}

# This target runs tests
install: clean
	mvn install

# This target does not run tests
install_native: clean
	JAVA_HOME=${GRAALVM_HOME} mvn -Dmaven.test.skip=true -DskipTests -Pnative install

clean:
	mvn clean

ifndef LIAR_REPOSITORY
$(error Please set LIAR_REPOSITORY !)
endif
