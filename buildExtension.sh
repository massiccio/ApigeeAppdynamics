#!/bin/bash

gradle clean build
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
