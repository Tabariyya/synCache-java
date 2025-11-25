#!/bin/zsh

cd "$(dirname "$0")/.."
rm -rf SynCache || true
git clone git@github.com:synCache-org/SynCache.git