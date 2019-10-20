GRAAL_VM_HOME=/home/pyfourmond/Programs/graalvm_rc16/graalvm-ce-1.0.0-rc16/

LIAR_UPDATE_REPOSITORY=org.grumpyf0x48.liar.update.SoftwareUpdateRepository
LIAR_SOFTWARE=${HOME}/Sources/liar/.liar_software

clean:
	mvn clean

install: clean
	mvn install

update: install
	mvn exec:java \
		-Dexec.mainClass=${LIAR_UPDATE_REPOSITORY} \
		-Dexec.args=${LIAR_SOFTWARE}

# To install Graalvm:
# 1. liar install graalvm https://github.com/oracle/graal/releases/download/vm-1.0.0-rc16/graalvm-ce-1.0.0-rc16-linux-amd64.tar.gz
# 2. sudo apt-get install zlib1g-de

install_native: clean
	JAVA_HOME=${GRAAL_VM_HOME} mvn -Pnative install

update_native: install_native
	./target/SoftwareUpdateRepository.native \
		-Djava.library.path=${JAVA_HOME}/jre/lib/amd64 \
		-Djavax.net.ssl.trustStore=${JAVA_HOME}/jre/lib/security/cacerts \
		${LIAR_SOFTWARE}
