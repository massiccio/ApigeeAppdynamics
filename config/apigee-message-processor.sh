#!/bin/bash

# Copyright 2019 michele@apache.org
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

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
