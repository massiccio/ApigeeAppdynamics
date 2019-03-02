#!/bin/bash

# This script assumes that the message processor has JMX enabled on port 12345
# If that is not the case, then change JMX_URL accordingly.


JMX_URL="service:jmx:rmi:///jndi/rmi://127.0.0.1:12345/jmxrmi"

# If empty, then name=Custom Metrics|Apigee| is used as default prefix
PREFIX="myprefix|"

java -jar -Djmx.url=$JMX_URL ApigeeMessageProcessor.jar $PREFIX
exit_status=$?

if [ $exit_status -eq 1 ]; then
    echo "Error while retrieving metrics via JMX."
fi

exit $exit_status
