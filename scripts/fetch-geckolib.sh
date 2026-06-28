#!/bin/sh
set -e
mkdir -p libs
curl -fsSL "https://cdn.modrinth.com/data/8BmcQJ2H/versions/L6bn4TS8/geckolib-fabric-26.2-5.5.3.jar" \
  -o "libs/geckolib-fabric-26.2-5.5.3.jar"
