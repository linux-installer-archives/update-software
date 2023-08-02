# liar-update-software

## How to add a new handled software

### 1. Update sources

Update [SoftwareDefinition.java](../src/main/java/org/grumpyf0x48/liar/update/SoftwareDefinition.java) to add the new software.

### 2. Update tests

Update [SoftwareUpdateServiceTest.java](../src/test/java/org/grumpyf0x48/liar/update/SoftwareUpdateServiceTest.java)

Update `checkUpdatableSoftwareTest` and `getSoftwareListTest` to increase count.

### 3. Update test files

Update the following files :

- [liar-software.expected](../src/test/resources/liar-software.expected) with the latest version of the software
- [liar-software](../src/test/resources/liar-software) with the previous version of the software

## Use a different Java than the system one

This project uses Java 11.

If the 'ci' machine uses another Java version, for example Java 13, we may not want to use this version to build the project.

So, in order to keep building this project with Java 11:

add to `Makefile`

```makefile
JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
MVN=JAVA_HOME=${JAVA_HOME} PATH=${JAVA_HOME}/bin:${PATH} mvn -X
```

or add to `/etc/anacrontab`

```sh
JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
PATH=${JAVA_HOME}/bin:/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin
```
