#!/bin/bash

# To be called in crontab
# 45 7 * * Tue,Fri ~/Sources/liar-update-software/liar-update-software.sh ~/Sources/liar/

# If maven is installed in user mode
PATH=~/bin:$PATH

liar_update_repository=$(dirname "$0")
exec > "${liar_update_repository}/liar-update-software.log" 2>&1

# Repository to update
liar_repository="$1"

# Run update
cd "${liar_update_repository}"
make LIAR_REPOSITORY="${liar_repository}" update

# Update repository
cd "${liar_repository}"
liar_software=.liar_software
if git status --porcelain ${liar_software} | grep ${liar_software}; then
    git add ${liar_software}
    git commit -m"Automatic URL update"
    git push
fi
