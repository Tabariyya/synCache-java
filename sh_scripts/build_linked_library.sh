#!/bin/zsh
ARCH=$(uname -m)
echo "$ARCH"
if [ "$ARCH" = "aarch64" ]; then
    filename="arm64"
elif [ "$ARCH" = "arm64" ]; then
    filename="arm64"
elif [ "$ARCH" = "x86_64" ]; then
    filename="x64"
elif [ "$ARCH" = "amd64" ]; then
    filename="x64"
else
    echo "invalid arch"
    exit 1;
fi

cd "$(dirname "$0")"
bash ../SynCache/bindings/java/build.sh

mv ../SynCache/bindings/java/libjavaSynCache.so ../src/main/resources/lib/linux/${filename}/synCache
