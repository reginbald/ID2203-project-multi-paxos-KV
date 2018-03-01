# ID2203 Project

This repository contains code for the implementation of a distributed in memory key-value store and is apart of the final project in the course ID2203 Distributed Systems, Advanced Course. 
The project makes use of Kompics which is a programming model for distributed systems that implements protocols as event-driven components connected by channels

## System Structure
Our system is made up of nodes, a node is essentially a single instance of a server. Each node in the system is assigned a partition. Nodes within a partition are replicated and are responsible for a partition of the key space. Within a partition a leader node proposes an operation to the other nodes using the multi-paxos algorithm, if an operation reaches consensus all participating nodes perform the operation.

A Client is able to connect to any node in the system and start sending commands, a node that is not the designated leader for a partition will forward the request to the partition leader. Nodes receiving messages intended for another partition forward the messages to the leader of the corresponding partition. Figure 1 shows how the system looks like when we have 2 partitions and the replication degree is set to δ = 3.

Each node is composed of various components with unique responsibilities. Each component is connected via ports and channels which transmit messages between the components and the outside world. These messages trigger event handlers who process the messages. Figure 2 depicts the inner structure of a single node in our system and how the components are connected.

## Overview

The project is split into 3 sub parts:

- A common library shared between servers and clients, containing mostly messages and similar shared types
- A server library that manages bootstrapping and membership
- A client library with a simple CLI to interact with a cluster of servers

The bootstrapping procedure for the servers, requires one server to be marked as a bootstrap server, which the other servers (bootstrap clients) check in with, before the system starts up. The bootstrap server also assigns initial partitions.

## Getting Started

Clone (your fork of) the repository to your local machine and cd into that folder.

### Building
Build the project with

```
maven clean install
```

### Running

#### Bootstrap Server Node
To run a bootstrap server node `cd` into the `server` directory and execute:

```
java -jar target/project17-server-1.0-SNAPSHOT-shaded.jar -p 45678
```

This will start the bootstrap server on localhost:45678.

#### Normal Server Node
After you started a bootstrap server on `<bsip>:<bsport>`, again from the `server` directory execute:

```
java -jar target/project17-server-1.0-SNAPSHOT-shaded.jar -p 56789 -c <bsip>:<bsport>
```
This will start the bootstrap server on localhost:56789, and ask it to connect to the bootstrap server at `<bsip>:<bsport>`.
Make sure you start every node on a different port if they are all running directly on the local machine.

By default you need 3 nodes (including the bootstrap server), before the system will actually generate a lookup table and allow you to interact with it.
The number can be changed in the configuration file (cf. [Kompics docs](http://kompics.sics.se/current/tutorial/networking/basic/basic.html#cleanup-config-files-classmatchers-and-assembly) for background on Kompics configurations).

#### Clients
To start a client (after the cluster is properly running), `cd` into the `client` directory and execute:

```
java -jar target/project17-client-1.0-SNAPSHOT-shaded.jar -p 56787 -b <bsip>:<bsport>
```

Again, make sure not to double allocate ports on the same machine.

The client will attempt to contact the bootstrap server and give you a small command promt if successful. Type `help` to see the available commands.

