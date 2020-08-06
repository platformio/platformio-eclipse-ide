<img src="https://cdn.platformio.org/images/platformio-logo.17fdc3bc.png" width="48px" height="48px" />

# PlatformIO Eclipse IDE

[![License](https://img.shields.io/badge/License-Apache--2.0-brightgreen.svg)](https://github.com/platformio/platformio-eclipse-ide/blob/master/LICENSE)
[![Build Status](https://github.com/platformio/platformio-eclipse-ide/workflows/CI/badge.svg)](https://github.com/platformio/platformio-eclipse-ide/actions)

PlatformIO IDE for Eclipse: The next generation integrated development environment for IoT

## Building

#### Prerequisites:
 1. Java (JDK) 1.8+
 2. Apache Maven 3.6.3
 3. Internet access

#### Build
```sh
git clone https://github.com/platformio/platformio-eclipse-ide.git
cd platformio-eclipse-ide
mvn verify
```
Produced p2 repository can be found at `repository/org.platformio.eclipse.ide.repository/target`