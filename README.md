# ApigeeAppdynamics
AppDynamics [extension](https://docs.appdynamics.com/display/PRO45/Build+a+Monitoring+Extension+Using+Scripts) to collect [Apigee Message Processor ](https://docs.apigee.com/private-cloud/v4.18.01/how-monitor) metrics.
Apart from the code providing the business logic, this project includes the AppDynamics files needed to build the extension
1. The AppDynamics extension configuration file, [monitor.xml](https://github.com/massiccio/ApigeeAppdynamics/blob/master/config/monitor.xml)
2. The [Bash script]() used to run the code

Also, a bash script building the code, running all tests and packaging the extension is [provided](https://github.com/massiccio/ApigeeAppdynamics/blob/master/buildExtension.sh).

A sample service used to show how the code works can be executed using gradle. Open two consoles:
1. On the first one type
```bash
gradle runFakeService
```
2. While on the second one type
```bash
gradle run

> Task :run
name=Custom Metrics|Apigee|faults|FaultyOrg|FaultyEnv|FaultyApi|PolicyErrors, value=1
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|MyApi_V3|RequestCount, value=2
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|MyApi_V4|RequestCount, value=3
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|MyApi_V3|ResponseCount, value=4
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|MyApi_V4|ResponseCount, value=5
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|MyApi_V3|ResponsesSent_5XX, value=6
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|MyApi_V4|ResponsesSent_5XX, value=7
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|MyApi_V3|ResponsesSent_5XX_pct, value=150
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|MyApi_V3|PendingRequests_pct,  value=0
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|MyApi_V4|ResponsesSent_5XX_pct, value=140
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|MyApi_V4|PendingRequests_pct,  value=0
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|ResponsesSent_5XX_pct, value=144
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|PendingRequests_pct,  value=0
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|ResponseCount, value=9
name=Custom Metrics|Apigee|inboundtraffic|MyOrg|MyEnv|RequestCount, value=5
$ echo $?
0
```

## Metrics

Metrics are collected via JMX. Unfortunately Apigee exposes MBeans in a lazy manner and removes them eagerly.
In other words, statistics are available only for APIs currently serving user requests.
For each API and for each Environment, the extensions compute the number requests and responses, as well as the number of pending jobs (i.e., arrivals - departures).
Also, statistics regarding the HTTP status codes are provided in terms of rates (i.e., requests/minute) as well as expressed in percentage of the incoming traffic (i.e., ResponsesSent_5XX_pct tells us the percentage of responses completed with a 5XX code).
This enables the creation of health rules of the type "Alert me whenever the percentage of HTTP 500 responses exceeds 10%".
