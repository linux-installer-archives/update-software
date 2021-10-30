GRAALVM_HOME=$(shell find /usr/lib/jvm/graalvm-ce-java11 -name native-image | grep -v lib/svm | head -n 1 | xargs dirname | xargs dirname)

ifeq (${GRAALVM_HOME},)
JAVA_HOME=$(shell find /usr/lib/jvm/java-11* -name java | head -n 1 | xargs dirname | xargs dirname)
else
JAVA_HOME=${GRAALVM_HOME}
endif

MVN=JAVA_HOME=${JAVA_HOME} PATH=${JAVA_HOME}/bin:${HOME}/bin:${PATH} mvn
MVN_NO_TESTS=-DskipTests -Dmaven.test.skip=true

build:
	${MVN} ${MVN_NO_TESTS} install

build_native:
	${MVN} ${MVN_NO_TESTS} -Pnative install

test:
	${MVN} install

check_software: build
	${MVN} exec:java \
		-Dexec.mainClass=org.grumpyf0x48.liar.update.SoftwareCheckRepository \
		-Dexec.args="${LIAR_REPOSITORY}/config/liar-software"

update_software: build
	${MVN} exec:java \
		-Dexec.mainClass=org.grumpyf0x48.liar.update.SoftwareUpdateRepository \
		-Dexec.args="${LIAR_REPOSITORY}/config/liar-software ${LIAR_PERIODICITY}"

update_software_native:
	./target/SoftwareUpdateRepository.native \
		-Djava.library.path=${GRAALVM_HOME}/jre/lib/amd64 \
		-Djavax.net.ssl.trustStore=${GRAALVM_HOME}/jre/lib/security/cacerts \
		"${LIAR_REPOSITORY}/config/liar-software" \
		${LIAR_PERIODICITY}

display-plugin-updates:
	${MVN} versions:display-plugin-updates

display-dependency-updates:
	${MVN} versions:display-dependency-updates

clean:
	${MVN} clean

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

LIAR_UPDATE_SOFTWARE=liar-update-software
USERID=$(shell id -u)
