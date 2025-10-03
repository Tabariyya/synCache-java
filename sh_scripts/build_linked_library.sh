#!/bin/zsh
ARCH=$(uname -m)
echo "$ARCH"
if [ "$ARCH" = "aarch64" ]; then
    filename="arm"
elif [ "$ARCH" = "arm64" ]; then
    filename="arm"
elif [ "$ARCH" = "x86_64" ]; then
    filename="x64"
elif [ "$ARCH" = "amd64" ]; then
    filename="x64"
else
    echo "invalid arch"
    exit 1;
fi

cd "$(dirname "$0")/../SynCache"
docker build -t syncache-temp .
docker create --name tmpcontainer syncache-temp

docker cp tmpcontainer:/app/libjavaSynCache.so ../src/main/resources/lib/linux/${filename}/libjavaSynCache.so
docker rm tmpcontainer
