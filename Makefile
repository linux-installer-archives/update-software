GRAALVM_HOME=$(shell find /usr/lib/jvm/graalvm-ce-java11 -name native-image | grep -v lib/svm | head -n 1 | xargs dirname | xargs dirname)
ifeq (${GRAALVM_HOME},)
JAVA_HOME=$(shell find /usr/lib/jvm/java-11* -name java | head -n 1 | xargs dirname | xargs dirname)
else
JAVA_HOME=${GRAALVM_HOME}
endif

ARCH=$(shell arch)
ifeq (${ARCH}, x86_64)
MEMORY_OPTS="-Xms512m -Xmx8192m"
else
MEMORY_OPTS="-Xms512m -Xmx768m"
endif

MVN=JAVA_HOME=${JAVA_HOME} PATH=${JAVA_HOME}/bin:${HOME}/bin:${PATH} MAVEN_OPTS=${MEMORY_OPTS} ./mvnw
MVN_NO_TESTS=-DskipTests -Dmaven.test.skip=true
NATIVE_TARGET=./target/SoftwareUpdateRepository.native

build:
	${MVN} ${MVN_NO_TESTS} install

build_native: ${NATIVE_TARGET}

${NATIVE_TARGET}:
	${MVN} ${MVN_NO_TESTS} -Pnative install

test:
	${MVN} install

check_software: build
	${MVN} exec:java \
		-Dexec.mainClass=org.grumpyf0x48.liar.update.cli.SoftwareCheckRepository \
		-Dexec.args="${LIAR_SOFTWARE}"

update_software: build
	${MVN} exec:java \
		-Dexec.cleanupDaemonThreads=false \
		-Dexec.mainClass=org.grumpyf0x48.liar.update.cli.SoftwareUpdateRepository \
		-Dexec.args="${LIAR_SOFTWARE} ${LIAR_PERIODICITY}"

update_software_native: build_native
	${NATIVE_TARGET} \
		-Djava.library.path=${GRAALVM_HOME}/jre/lib/amd64 \
		-Djavax.net.ssl.trustStore=${GRAALVM_HOME}/jre/lib/security/cacerts \
		${LIAR_SOFTWARE} ${LIAR_PERIODICITY}

display-plugin-updates:
	${MVN} versions:display-plugin-updates

display-dependency-updates:
	${MVN} versions:display-dependency-updates

clean:
	${MVN} clean

install: install_liar_update_software install_all_crons

install_liar_update_software:
	sudo cp --verbose ${LIAR_UPDATE_SOFTWARE} /usr/local/bin

install_all_crons: uninstall_all_crons install_all_crons_${ARCH}

install_all_crons_armv7l:
	make LIAR_PERIODICITY=daily install_cron
	make LIAR_PERIODICITY=weekly install_cron

install_all_crons_x86_64:
	make LIAR_PERIODICITY=monthly install_cron

uninstall_all_crons:
	make LIAR_PERIODICITY=daily uninstall_cron
	make LIAR_PERIODICITY=weekly uninstall_cron
	make LIAR_PERIODICITY=monthly uninstall_cron

install_cron:
	sudo cp --verbose ${LIAR_UPDATE_SOFTWARE}-${LIAR_PERIODICITY} /etc/cron.${LIAR_PERIODICITY}/${LIAR_UPDATE_SOFTWARE}
	sudo chmod -v +x /etc/cron.${LIAR_PERIODICITY}/${LIAR_UPDATE_SOFTWARE}

uninstall_cron:
	sudo rm --verbose --force /etc/cron.${LIAR_PERIODICITY}/${LIAR_UPDATE_SOFTWARE}

ifdef LIAR_REPOSITORY
LIAR_SOFTWARE=${LIAR_REPOSITORY}/config/liar-software
endif

ifndef LIAR_PERIODICITY
LIAR_PERIODICITY=monthly
endif

LIAR_UPDATE_SOFTWARE=liar-update-software
