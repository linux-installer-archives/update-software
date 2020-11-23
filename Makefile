LIAR_SOFTWARE=${LIAR_REPOSITORY}/liar-software
LIAR_UPDATE_SOFTWARE=liar-update-software
USERID:=$(shell id -u)

install: install_liar_update_software install_all_crons

install_liar_update_software:
	sudo cp --verbose --update ${LIAR_UPDATE_SOFTWARE} /usr/local/bin

install_all_crons:
	make LIAR_PERIODICITY=daily install_cron
	make LIAR_PERIODICITY=weekly install_cron
	make LIAR_PERIODICITY=monthly install_cron

install_cron:
	echo "#!/usr/bin/env bash" | sudo tee /etc/cron.${LIAR_PERIODICITY}/${LIAR_UPDATE_SOFTWARE}
	echo "sudo -u #${USERID} ${LIAR_UPDATE_SOFTWARE} ${LIAR_PERIODICITY} >/var/log/${LIAR_UPDATE_SOFTWARE}-${LIAR_PERIODICITY}.log 2>&1" | sudo tee -a /etc/cron.${LIAR_PERIODICITY}/${LIAR_UPDATE_SOFTWARE}

# This target does not run tests
build_native: clean
	JAVA_HOME=${GRAALVM_HOME} mvn -Dmaven.test.skip=true -DskipTests -Pnative install

# This target runs tests
build: clean
	mvn install

test: build

# This target does not depend on install_native because it is very long to build
update_software_native: build_native
	ifndef LIAR_SOFTWARE
	$(error Please set LIAR_SOFTWARE !)
	endif
	./target/SoftwareUpdateRepository.native \
		-Djava.library.path=${JAVA_HOME}/jre/lib/amd64 \
		-Djavax.net.ssl.trustStore=${JAVA_HOME}/jre/lib/security/cacerts \
		${LIAR_SOFTWARE} \
		${LIAR_PERIODICITY}

update_software: build
	ifndef LIAR_SOFTWARE
	$(error Please set LIAR_SOFTWARE !)
	endif
	mvn exec:java \
		-Dexec.mainClass=org.grumpyf0x48.liar.update.SoftwareUpdateRepository \
		-Dexec.args="${LIAR_SOFTWARE} ${LIAR_PERIODICITY}"

clean:
	mvn clean

ifndef LIAR_PERIODICITY
LIAR_PERIODICITY=monthly
endif
