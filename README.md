# FlightsAPI

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

Flights API to get the flight information. Flight information can also be filtered based on different fields.

---

## Table of Contents

- [Features](#features)
- [Notes](#Notes)
- [Installation](#installation)
- [Usage](#usage)
- [APISpecification](#ApiSpecification)
- [Tests](#tests)
- [License](#license)
- [Contact](#contact)

---

## Features

- ✅ Retrieve all flight information
- ✅ Retrieve flight information based on filters- destination, scheduleDateTime, direction, and status
- ✅ Retrieve flight information based on combination of above filters

---

## Notes

Some choice have been made for developing this project. For elastic, testcontainers has been used for this project so that just docker needs to be running in the machine 
and elastic image will be downloaded and container will be started there. This way elastic image does not have to be downloaded manually.

There also some improvements can be made, e.g. using different models for REST endpoint and elastic-data and so on. The idea is to have a nice discussion 
around the code and technical approach. Would love to listen to other ideas and discuss them.

---

## Installation

Follow these steps to set up the project locally:

### Prerequisites
- Java 17+
- Maven
- Docker should be up and running

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/agarwalvibhor1914/FlightsAPI.git
2. Navigate to the project directory
3. Install dependencies and build project:
- For Maven:
  ```
  mvn clean install
  ```
---

## Usage

### Run the Application
To start the project, execute:
```
java -jar target/flightsAPI-0.0.1-SNAPSHOT.jar
```

### Swagger-ui
Swagger-ui has been enable for this project and can be accessed at url-

http://localhost:8080/swagger-ui/index.html#

Example request to get all the flights-

curl -X GET http://localhost:8080/flights

---

## APISpecification
Specifications and structure about the end point can be found at the url (Please check pretty-print box)-

http://localhost:8080/v3/api-docs

## Tests
There are Unit test cases and integration test written for this project. Tu run them, please execute-
```
mvn test
```
Right now test case coverage is almost 100%.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Contact

For questions or support, please contact:

- Author: Vibhor Agarwal
- Email: agarwalvibhor1914@gmail.com