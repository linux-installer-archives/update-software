LIAR_SOFTWARE=${LIAR_REPOSITORY_HOME}/.liar_software

update: install
	mvn exec:java \
		-Dexec.mainClass=org.grumpyf0x48.liar.update.SoftwareUpdateRepository \
		-Dexec.args=${LIAR_SOFTWARE}

update_native: install_native
	./target/SoftwareUpdateRepository.native \
		-Djava.library.path=${JAVA_HOME}/jre/lib/amd64 \
		-Djavax.net.ssl.trustStore=${JAVA_HOME}/jre/lib/security/cacerts \
		${LIAR_SOFTWARE}

install: clean
	mvn install

install_native: clean
	JAVA_HOME=${GRAAL_VM_HOME} mvn -Pnative install

clean:
	mvn clean

ifndef LIAR_REPOSITORY_HOME
$(error Please set LIAR_REPOSITORY_HOME !)
endif
