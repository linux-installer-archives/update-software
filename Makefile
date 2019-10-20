LIAR_SOFTWARE=${LIAR_REPOSITORY}/.liar_software

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

ifndef LIAR_REPOSITORY
$(error Please set LIAR_REPOSITORY !)
endif

# To install Graalvm:
#
# liar install graalvm https://github.com/oracle/graal/releases/download/vm-1.0.0-rc16/graalvm-ce-1.0.0-rc16-linux-amd64.tar.gz
# sudo apt-get install zlib1g-dev
# GRAAL_VM_HOME=~/Programs/graalvm_rc16/graalvm-ce-1.0.0-rc16/
#
