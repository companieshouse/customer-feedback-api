#!/bin/bash
#
# Start script for customer-feedback-api

PORT=8080

exec java -jar -Dserver.port="${PORT}" "customer-feedback-api.jar"
