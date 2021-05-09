GRAALVM_HOME=$(shell find /usr/lib/jvm -name native-image | grep -v lib/svm | xargs dirname | xargs dirname)
USERID=$(shell id -u)

LIAR_UPDATE_SOFTWARE=liar-update-software
MVN_NO_TESTS=-DskipTests -Dmaven.test.skip=true

build:
	mvn ${MVN_NO_TESTS} install

build_native:
	JAVA_HOME=${GRAALVM_HOME} mvn ${MVN_NO_TESTS} -Pnative install

test:
	mvn install

update_software: build
	mvn exec:java \
		-Dexec.mainClass=org.grumpyf0x48.liar.update.SoftwareUpdateRepository \
		-Dexec.args="${LIAR_REPOSITORY}/config/liar-software ${LIAR_PERIODICITY}"

update_software_native:
	./target/SoftwareUpdateRepository.native \
		-Djava.library.path=${GRAALVM_HOME}/jre/lib/amd64 \
		-Djavax.net.ssl.trustStore=${GRAALVM_HOME}/jre/lib/security/cacerts \
		${LIAR_REPOSITORY}/config/liar-software \
		${LIAR_PERIODICITY}

display-updates: display-plugin-updates display-dependency-updates

display-plugin-updates:
	mvn versions:display-plugin-updates

display-dependency-updates:
	mvn versions:display-dependency-updates

clean:
	mvn clean

install: install_liar_update_software install_all_crons

install_liar_update_software:
	sudo cp --verbose ${LIAR_UPDATE_SOFTWARE} /usr/local/bin

install_all_crons:
	make LIAR_PERIODICITY=daily install_cron
	make LIAR_PERIODICITY=weekly install_cron
	make LIAR_PERIODICITY=monthly install_cron

install_cron:
	echo "#!/usr/bin/env bash\nsudo -u \#${USERID} ${LIAR_UPDATE_SOFTWARE} ${LIAR_PERIODICITY} >/var/log/${LIAR_UPDATE_SOFTWARE}-${LIAR_PERIODICITY}.log 2>&1" \
		| sudo tee /etc/cron.${LIAR_PERIODICITY}/${LIAR_UPDATE_SOFTWARE}
	sudo chmod +x /etc/cron.${LIAR_PERIODICITY}/${LIAR_UPDATE_SOFTWARE}

ifndef LIAR_PERIODICITY
LIAR_PERIODICITY=monthly
endif
