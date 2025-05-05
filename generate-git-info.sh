#!/bin/bash

COMMIT_HASH=$(git log origin/main --no-merges -n 1 --pretty=format:"%H")
COMMIT_MESSAGE=$(git log origin/main --no-merges -n 1 --pretty=format:"%s")
COMMIT_AUTHOR=$(git log origin/main --no-merges -n 1 --pretty=format:"%an")
COMMIT_TIME=$(git log origin/main --no-merges -n 1 --pretty=format:"%ci")

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

echo "== Git Commit Info =="
echo "Full Commit: $COMMIT_HASH"
echo "Message: $COMMIT_MESSAGE"
echo "Author: $COMMIT_AUTHOR"
echo "Time: $COMMIT_TIME"