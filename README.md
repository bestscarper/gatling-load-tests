gatling-load-tests 
==================

(ash - test write)
(ash - test write2)

## Description

This project contains [Gatling](http://gatling.io/#/) simulations used 
by the BBC Digital load-test team. 

## Getting Started
Clone the [Gatling SBT plugin demo](https://github.com/gatling/gatling-sbt-plugin-demo) to begin running your own simulations. 
## Real-time metrics
### gatling.conf
```
data {
  writers = "console, file, graphite"
}
graphite {
   host = "192.0.2.235" 
   port = 2003
   protocol = "tcp"
   rootPathPrefix = "gatling"
}
```

### Grafana/InfluxDB

I would suggest running the Docker containers from [CoreOS](https://coreos.com/). 

```bash
docker run -d -p 8081:8081 --name grafana aidylewis/grafana
docker run -d \
           -p 8083:8083 -p 8084:8084 -p 8086:8086 -p 2003:2003 \
           -e PRE_CREATE_DB="gatling;grafana" --name influxdb davey/influxdb:latest
```

## Gatling Jenkins 
```bash 
docker run -d -p 8080:8080 --name gatling-jenkins aidylewis/gatling-jenkins
```
