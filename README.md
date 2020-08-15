# liar-update-software

## How to add a new handled software

### 1. Update SoftwareDefinition

Add the new software.

### 2. Update SoftwareUpdateServiceTest

Update `checkUpdatableSoftwareTest` and `getSoftwareListTest` to increase count.

### 3. Update .liar_software files

Add the new software in `.liar_software` and `.liar_software.expected`.

### 4. Update .liar_software in $HOME

    $ cp ${LIAR_REPOSITORY}/.liar_software ~
