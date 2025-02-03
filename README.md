# Load Balancer Application

This project implements a load balancer in Java using Spring Boot, along with backend servers. The load balancer distributes incoming requests to multiple backend servers based on the selected algorithm (e.g., round-robin, random). Additionally, a Python version of the backend server is provided for demonstration purposes.

## Table of Contents
1. [Setup Instructions](#setup-instructions)
    - [Prerequisites](#prerequisites)
    - [Project Structure](#project-structure)
    - [Building the Project](#building-the-project)
    - [Running the Load Balancer](#running-the-load-balancer)
    - [Running Python Backend Servers](#running-python-backend-servers)
2. [API Endpoints](#api-endpoints)
    - [Load Balancer APIs](#load-balancer-apis)
    - [Backend Server APIs](#backend-server-apis)
3. [High-Level Design (HLD)](#high-level-design-hld)
4. [Low-Level Design (LLD)](#low-level-design-lld)

---

## Setup Instructions

### Prerequisites
- **Java Development Kit (JDK) 11 or higher**: Ensure that Java is installed on your system. You can download it from [Oracle's official website](https://www.oracle.com/java/technologies/javase-downloads.html).
- **Apache Maven**: This project uses Maven for build automation. You can download it from the [official Maven website](https://maven.apache.org/download.cgi).
- **Python 3.x** (for running the Python backend servers): Download it from the [official Python website](https://www.python.org/downloads/).

### Project Structure
The project is organized into 1 module:
- **load-balancer-application**: Contains the code for the load balancer.

```plaintext
load-balancer/
├── load-balancer-application/
│   ├── src/
│   ├── pom.xml
│   └── ...
├── backend_server.py
├── parent_pom.xml
└── ...
```

### Building the Project
Clone the Repository:

```
git clone https://github.com/mohitraj007/load-balancer.git
cd load-balancer-project
```

### Build the Modules:

Use Maven to build both modules. Ensure you have Maven installed and configured in your system's PATH.


```mvn clean install```

This command will compile the code and package the applications into JAR files located in the target directories of each module.

### Running the Load Balancer
Navigate to the load-balancer-application directory and run the application:

```
cd load-balancer-application
java -jar target/load-balancer-application-0.0.1-SNAPSHOT.jar
```

By default, the load balancer will run on port 8080. You can specify a different port by adding the `--server.port` parameter:


``` java -jar target/load-balancer-application.jar --server.port=8081 ```

### Running Python Backend Servers
A Python version of the backend server is available for demonstration purposes.

Navigate to the Python Server Directory:

```cd python-backend-server```

Install Required Dependencies:
Ensure you have Flask installed. You can install it using pip:

```pip install Flask```

Run Multiple Instances:

Open multiple terminal windows or tabs and run the script on different ports:
```
python backend-server.py 8081
python backend-server.py 8082
python backend-server.py 8083
```

Each command starts a backend server instance on the specified port.

## API Endpoints

### Load Balancer APIs

#### 1. Set Load Balancing Algorithm:

> Endpoint: /loadbalancer/setAlgorithm
>
> Method: POST
>
> Parameters: algo (query parameter): The load balancing algorithm to use (round-robin or random).
>
> Sample Request: curl -X POST "http://localhost:8080/loadbalancer/setAlgorithm?algo=round-robin"

#### 2. Forward Request:

> Endpoint: /loadbalancer/proxy
>
> Method: GET
>
> Parameters: path (query parameter): The path to forward the request to on the selected backend server.
>
> Sample Request: curl "http://localhost:8080/loadbalancer/proxy?path=/data"

#### 3. List backend servers:

> Endpoint: /loadbalancer/servers
>
> Method: GET
>
> Sample Request: curl -X GET "http://localhost:8080/loadbalancer/servers"

#### 4. Add backend server:

> Endpoint: /loadbalancer/servers
>
> Method: POST
>
> Parameters: serverUrl (query parameter): The server URL to add.
>
> Sample Request: curl -X POST "http://localhost:8080/loadbalancer/servers?serverUrl=localhost:8088"

#### 5. Remove backend server:

> Endpoint: /loadbalancer/servers
>
> Method: DELETE
>
> Parameters: serverUrl (query parameter): The server URL to delete.
>
> Sample Request: curl -X DELETE "http://localhost:8080/loadbalancer/servers?serverUrl=localhost:8088"

### Backend Server APIs
#### 1. Health Check:

> Endpoint: /health
>
> Method: GET
>
> Description: Returns the health status of the backend server. Alternates between OK and FAIL every 10 seconds.

#### 2. Get Data:

> Endpoint: /data
>
> Method: GET
>
> Description: Returns a simple response with the current timestamp.

## High-Level Design (HLD)
The application consists of a load balancer that distributes incoming client requests to multiple backend servers. The load balancer supports different algorithms for distributing requests, such as round-robin and random selection. It also performs periodic health checks to ensure that only healthy backend servers receive traffic.

## Low-Level Design (LLD)
- LoadBalancerController: Handles incoming API requests related to load balancing, such as setting the algorithm and forwarding requests.

- LoadBalancerService: Contains the core logic for selecting backend servers based on the chosen algorithm and forwarding client requests. It also performs health checks on the backend servers at regular intervals.

- LoadBalancingAlgorithm Interface: Defines the contract for different load balancing algorithms. Implementations include RoundRobinAlgorithm and RandomAlgorithm.

For detailed code implementation, please refer to the respective source files in the project repository.