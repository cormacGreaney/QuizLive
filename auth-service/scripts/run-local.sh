#!/usr/bin/env bash
set -euo pipefail
export DB_USERNAME=${DB_USERNAME:-root}
export DB_PASSWORD=${DB_PASSWORD:-password}
export JWT_SECRET=${JWT_SECRET:-'this-should-be-32+chars-long-change-me-1234567890'}
export GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID:-'YOUR_GOOGLE_CLIENT_ID'}
export GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET:-'YOUR_GOOGLE_CLIENT_SECRET'}
./mvnw spring-boot:run || mvn spring-boot:run
