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


gradle clean build test
exit_status=$?

if [ $exit_status -eq 1 ]; then
  printf "\nError while building the library. Check the logs."
  exit $exit_status
fi

extension_dir="apigee-extension"
extension_file_name="apigee-extension.tar.gz"
mkdir $extension_dir
cp config/monitor.xml $extension_dir
cp config/apigee-message-processor.sh $extension_dir
cp build/libs/ApigeeMessageProcessor.jar $extension_dir

tar czf $extension_file_name $extension_dir
rm -fr $extension_dir
printf "\nCreated file $extension_file_name\n"
exit $?
