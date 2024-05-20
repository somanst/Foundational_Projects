#ifndef NETWORK_H
#define NETWORK_H

#include <vector>
#include <iostream>
#include "Packet.h"
#include "Client.h"

using namespace std;

class Network {
public:
    Network();
    ~Network();

    // Executes commands given as a vector of strings while utilizing the remaining arguments.
    void process_commands(vector<Client> &clients, vector<string> &commands, int message_limit, const string &sender_port,
                     const string &receiver_port);

    // Initialize the network from the input files.
    vector<Client> read_clients(string const &filename);
    void read_routing_tables(vector<Client> & clients, string const &filename);
    vector<string> read_commands(const string &filename);

    string getOriginialMessageChunk(stack<Packet*> frame);
    string getOriginialSenderID(stack<Packet*> frame);
    string getOriginialReceiverID(stack<Packet*> frame);
    int lookForClient(string macAdress, vector<Client> clients);
    int lookForRoute(string receiverID, vector<Client> clients, int sendIndex);
    string getTime();
    stack<Packet*> createFrame(string senderID, string receiverID, string mes, string sender_port, string receiver_port,vector<Client> clients);
    void printStack(stack<Packet*> frame);
    int getHop(stack<Packet*> frame);
};

#endif  // NETWORK_H
