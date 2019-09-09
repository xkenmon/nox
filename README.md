## Overview

Nox is a high performance shadowsocks implementation based on netty network framework.

## Features

- High performance throughput based on netty.
- Synchronous non-blocking IO model
    - Linux: epoll
    - MacOS: kqueue
    - Windows and other: netty default nio
- Server side supports multiple users.
- Only support TCP. (version 0.0.1-SNAPSHOT)

### Supported encryption methods

* aes-256-cfb
* aes-192-cfb
* aes-128-cfb
* aes-256-ctr
* aes-192-ctr
* aes-128-ctr

## Requirements

* Java 11

## Build

### Configuring the Maven toolchains plugin

[Guide to Using Toolchains](https://maven.apache.org/guides/mini/guide-using-toolchains.html)

### compile

`cd nox`

`./mvnw clean install`

### build server

`cd nox-server`

`mvn clean package`

### run server

`java -jar target/nox-server-{version}.jar`

### build client

`cd nox-client`

`mvn clean package`

### run client

`java -jar target/nox-client-{version}.jar`

## Usage

### Server

```
Usage: <main class> [options]
  Options:
    -c, --config
      special config file path (json format).
    --debug
      debug log level. [0: debug, 1: info, 2: warn, 3: error]
      Default: 1
    -h, --help
      show usage
    -l, --listen
      local listen address
    -m, --method
      encrytion method
    -k, --password
      password
    -p, --port
      server port
    -t, --timeout
      timeout in second

```

#### Config File Example

```json
{
  "listen_address": "0.0.0.0",
  "port_password": [
    {
      "port": 12345,
      "password": "custom-password"
    },
    {
      "port": 54321,
      "password": "other-password"
    }
  ],
  "method": "aes-256-cfb",
  "timeout": 300
}
```

### Client

```
Usage: <main class> [options]
  Options:
    -b, --bind
      local binding address
      Default: 127.0.0.1
    --debug
      debug log level. [0: debug, 1: info, 2: warn, 3: error]
      Default: 1
    -h, --help
      show usage
    -l, --listen
      local listen port
      Default: 11080
    -m, --method
      encrytion method
      Default: aes-256-cfb
  * -k, --password
      password
  * -p, --port
      server port
  * -s, --server
      server address
    -t, --timeout
      timeout in second
      Default: 300


```
