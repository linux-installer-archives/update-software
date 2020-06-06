LIAR_SOFTWARE=${LIAR_REPOSITORY}/.liar_software

update: install
	mvn exec:java \
		-Dexec.mainClass=org.grumpyf0x48.liar.update.SoftwareUpdateRepository \
		-Dexec.args=${LIAR_SOFTWARE}

# Do not make this target depend on install_native as it is very long to build
update_native:
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

ifndef LIAR_REPOSITORY
$(error Please set LIAR_REPOSITORY !)
endif
