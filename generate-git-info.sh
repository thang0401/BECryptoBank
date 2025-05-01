#!/bin/bash

COMMIT_HASH=$(git log --no-merges -n 1 --pretty=format:"%H")
COMMIT_MESSAGE=$(git log --no-merges -n 1 --pretty=format:"%s")
COMMIT_AUTHOR=$(git log --no-merges -n 1 --pretty=format:"%an")
COMMIT_TIME=$(git log --no-merges -n 1 --pretty=format:"%ci")

update_env() {
    local key="$1"
    local value="$2"
    if grep -q "^$key=" .env; then
        sed -i "s|^$key=.*|$key=$value|" .env
    else
        echo "$key=$value" >> .env
    fi
}

touch .env

update_env "GIT_COMMIT_ID_FULL" "$COMMIT_HASH"
update_env "GIT_COMMIT_MESSAGE_FULL" "$COMMIT_MESSAGE"
update_env "GIT_COMMIT_AUTHOR_NAME" "$COMMIT_AUTHOR"
update_env "GIT_COMMIT_TIME" "$COMMIT_TIME"