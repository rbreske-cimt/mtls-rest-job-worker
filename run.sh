#!/bin/bash

# set env variables
while read p; do
    echo "exporting $p"
    export "$p"
done < ./.env

# execute given commands
for cmd in "$@"; do
    echo "$cmd"
    eval "$cmd"
done

# unset env variables
while read p; do
    var="$(cut -d "=" -f1 <<< $p)"
    echo "unsetting $var"
    unset "$var"
done < ./.env
