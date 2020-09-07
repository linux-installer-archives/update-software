# liar-update-software

## How to add a new handled software

### 1. Update SoftwareDefinition

Add the new software.

### 2. Update SoftwareUpdateServiceTest

Update `checkUpdatableSoftwareTest` and `getSoftwareListTest` to increase count.

### 3. Update liar-software files in src/test/resources

Add the new software in `liar-software` and `liar-software.expected`.

### 4. Make sur liar-software is up to date

    $ cp ${LIAR_REPOSITORY}/liar-software $XDG_CONFIG_HOME/liar
