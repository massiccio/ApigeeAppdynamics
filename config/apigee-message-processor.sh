#!/bin/bash

# This script assumes that the message processor has JMX enabled on port 1101
# If that is not the case, then change JMX_URL accordingly.
# For more information, refer to the official documentation
# https://docs.apigee.com/private-cloud/v4.19.01/how-monitor


JMX_URL="service:jmx:rmi:///jndi/rmi://127.0.0.1:1101/jmxrmi"

# If empty, then name=Custom Metrics|Apigee| is used as default prefix
PREFIX="myprefix|"

java -jar -Djmx.url=$JMX_URL ApigeeMessageProcessor.jar $PREFIX
exit_status=$?

if [ $exit_status -eq 1 ]; then
    echo "Error while retrieving metrics via JMX."
fi

exit $exit_status
