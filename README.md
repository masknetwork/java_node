# MaskNetwork 
MaskNetwork is a peer to peer decentralized social trading network where traders and bloggers are rewarded for 
the content they create. MaskNetwork pays both the content creators when their work gets upvoted, as well as 
the people who curate the best content on the site by upvoting others work. The network can be accessed over 
the web so you don't have to download anything to start using it. Check www.masknetwork.com for more info.

## About this repositoire
A MaskNetwork node has 2 components. A java kernel (the daemon) and the web interface. Those two modules use MySQL as a 
communication method.This is the java daemon source code. Below are basic install instructions. Keep in mind that 
MaskNetwork live network is not active yet. After launching, the node will connect to MaskNetwork testnet. 

## Licence
MaskNetwork is released under the terms of the MIT license. Check https://opensource.org/licenses/MIT for more informations

## Prerequisites
- A linux OS : this software was tested and optimised to run on a Linux machine. We recommend a minimum 1GB memory box having 
at least 200 Mb of free space. 

- Java 8 SDK installed. In some cases you need to install Java Unilimited Strenght Encrytion. 
Check http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html for more info.

- MySQL 5.6

## Download 
You can download the latest stable binary from here www.masknetwork.com/wallet.zip or compile from source code. Unzip the 
files in a local directory (in case you downlloaded the binaries). 

## Installation
The first step is to create a MySQL database, create a user and assign the user the following rights (CREATE, DROP, 
SELECT, INSERT, DELETE, UPDATE).

Compile or download the jar file, and then run the following command

```
nohup java -Xmx512m -Xms512m -jar PATH_TO_JAR_FILE -db_user=user db_pass=pass db_name=name wallet_pass=wpass &
```

### Parameters
- **nohup** nohup will load the jar file in memory and run it in background as a daemon

- **-Xmx512m** maximum memory that can be used by node (we recommend a value equal to 50% of system memory). For a 1GB 
system use -Xmx512m, for a 2GB use -Xmx1024m and so on

- **-Xms512m** minimum memory that will used by node. We recommend a value equal to maximum memory.

- **PATH_TO_JAR_FILE** this is the path to jar file.

- **db_user** the MySQL username created at step 1

- **db_pass** the MySQL user password

- **db_name** the MySQL database name created at step 1

- **wallet_pass** the node will store the private keys in an **AES encrypted** file. This is the file password. If you 
loose this password, you will **loose** your coins and any asset associated.

When you launch the node for the first time, it will initialize the database by creating the default tables. Next 
it will connect to testnet and begin the sync process. Run this command to view the node log

```
tail -n100 nohup.out
```

All errors / log messages will be written to nohup.out In case the sync process freeze, try to relaunch the daemon. 

### Managing the node
In order to manage the node, you will need the web interface installed. The web interface allows you to access all network 
features an a easy way. As an admin you will also be able to stop the node, start / stop the integrated CPU miner and 
other admin tasks. Check web node README for detailed install instructions. 

### Manually stopping the daemon
To stop the node you need the process ID. In order to obtain the process ID, run this command

```
top
```

and then press Q. The process name is java. Copy the process ID and then kitt it using 

```
kill ID
```
