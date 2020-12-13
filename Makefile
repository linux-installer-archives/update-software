LIAR_UPDATE_SOFTWARE=liar-update-software
MVN_NO_TESTS=-DskipTests -Dmaven.test.skip=true 
USERID=$(shell id -u)

install: install_liar_update_software install_all_crons

install_liar_update_software:
	sudo cp --verbose --update ${LIAR_UPDATE_SOFTWARE} /usr/local/bin

install_all_crons:
	make LIAR_PERIODICITY=daily install_cron
	make LIAR_PERIODICITY=weekly install_cron
	make LIAR_PERIODICITY=monthly install_cron

install_cron:
	echo "#!/usr/bin/env bash\n"  \
		 "sudo -u \#${USERID} ${LIAR_UPDATE_SOFTWARE} ${LIAR_PERIODICITY} >/var/log/${LIAR_UPDATE_SOFTWARE}-${LIAR_PERIODICITY}.log 2>&1" \
			| sudo tee /etc/cron.${LIAR_PERIODICITY}/${LIAR_UPDATE_SOFTWARE}
	sudo chmod +x /etc/cron.${LIAR_PERIODICITY}/${LIAR_UPDATE_SOFTWARE}

# This target does not run tests
build_native:
	JAVA_HOME=${GRAALVM_HOME} mvn $(MVN_NO_TESTS) -Pnative install

# This target runs tests
build:
	mvn install

build_no_tests:
	mvn $(MVN_NO_TESTS) install

test: build

# This target does not depend on install_native because it is very long to build
update_software_native: build_native
	./target/SoftwareUpdateRepository.native \
		-Djava.library.path=${GRAALVM_HOME}/jre/lib/amd64 \
		-Djavax.net.ssl.trustStore=${GRAALVM_HOME}/jre/lib/security/cacerts \
		${LIAR_REPOSITORY}/liar-software \
		${LIAR_PERIODICITY}

update_software: build_no_tests
	mvn exec:java \
		-Dexec.mainClass=org.grumpyf0x48.liar.update.SoftwareUpdateRepository \
		-Dexec.args="${LIAR_REPOSITORY}/liar-software ${LIAR_PERIODICITY}"

clean:
	mvn clean

ifndef LIAR_PERIODICITY
LIAR_PERIODICITY=monthly
endif
