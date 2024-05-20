#include "Network.h"

Network::Network() {

}

void Network::process_commands(vector<Client> &clients, vector<string> &commands, int message_limit,
                      const string &sender_port, const string &receiver_port) {
    // TODO: Execute the commands given as a vector of strings while utilizing the remaining arguments.
    /* Don't use any static variables, assume this method will be called over and over during testing.
     Don't forget to update the necessary member variables after processing each command. For example,
     after the MESSAGE command, the outgoing queue of the sender must have the expected frames ready to send. */

    for (string command: commands) {
        std::string initialCommand;
        int i = 0;
        char curChar = command[i];
        while (curChar != ' ' && i < command.size()) {
            initialCommand += curChar;
            i++;
            curChar = command[i];
        }

        cout << string(command.size() + 9, '-') << "\n";
        cout << "COMMAND: " << command << "\n";
        cout << string(command.size() + 9, '-') << "\n";

        if (initialCommand == "MESSAGE") {
            string senderID = string(1, command[8]);
            string receiverID = string(1, command[10]);
            vector<string> messageChunks;
            string message;
            string fullMessage;

            i = 13;
            curChar = command[i];
            int charcount = 0;
            while (curChar != '#') {
                message += curChar;
                fullMessage += curChar;
                i++;
                charcount++;
                curChar = command[i];

                if (charcount == message_limit) {
                    messageChunks.push_back(message);
                    message.clear();
                    charcount = 0;
                }
            }

            cout << "Message to be sent: \"" << fullMessage << "\"\n\n";



            if (!message.empty()) messageChunks.push_back(message);
            int senderIndex = lookForClient(senderID, clients);

            int frameCount = 0;
            for (string mes: messageChunks) {
                frameCount++;
                cout << "Frame #" << frameCount << "\n";
                stack<Packet*> frame = createFrame(senderID, receiverID, mes, sender_port, receiver_port, clients);
                clients[senderIndex].outgoing_queue.push(frame);
            }
            clients[senderIndex].frameCountOutgoing.push(frameCount);
        }
        else if(initialCommand == "SEND") {
            for (int i = 0; i < clients.size(); i++) {
                string message;
                int frameCount_ = 0;
                while(!clients[i].outgoing_queue.empty()) {
                    string macAdress = dynamic_cast<PhysicalLayerPacket*> (clients[i].outgoing_queue.front().top())->receiver_MAC_address;
                    int recieveIndex = lookForClient(macAdress, clients);
                    dynamic_cast<PhysicalLayerPacket*> (clients[i].outgoing_queue.front().top())->incHop();
                    clients[recieveIndex].incoming_queue.push(clients[i].outgoing_queue.front());

                    cout << "Client " << clients[i].client_id << " sending frame #" << frameCount_ + 1 << " to client " << clients[recieveIndex].client_id << "\n";
                    printStack(clients[i].outgoing_queue.front());

                    message += getOriginialMessageChunk(clients[i].outgoing_queue.front());
                    int hop = getHop(clients[i].outgoing_queue.front());
                    frameCount_++;
                    string originalReceiverID = getOriginialReceiverID(clients[i].outgoing_queue.front());
                    string originalSenderID = getOriginialSenderID(clients[i].outgoing_queue.front());
                    clients[i].outgoing_queue.pop();

                    if(frameCount_ == clients[i].frameCountOutgoing.front()){
                        Log log = *new Log(getTime(), message, frameCount_, hop, originalSenderID, originalReceiverID, 1, ActivityType::MESSAGE_SENT);
                        clients[i].log_entries.push_back(log);
                        message.clear();
                        clients[recieveIndex].frameCountIncoming.push(frameCount_);
                        clients[i].frameCountOutgoing.pop();
                        frameCount_ = 0;
                    }
                }
            }
        }
        else if(initialCommand == "RECEIVE") {
            for (int i = 0; i < clients.size(); i++) {
                int frameCount_ = 0;
                string message;
                while (!clients[i].incoming_queue.empty()) {
                    bool forwarding = false;

                    string originalReceiverID = getOriginialReceiverID(clients[i].incoming_queue.front());
                    string originalSenderID = getOriginialSenderID(clients[i].incoming_queue.front());
                    string messageChunk = getOriginialMessageChunk(clients[i].incoming_queue.front());
                    int hop = getHop(clients[i].incoming_queue.front());

                    frameCount_++;
                    message += messageChunk;
                    if(originalReceiverID != clients[i].client_id){
                        forwarding = true;
                    }

                    if(!forwarding){
                        if(frameCount_ == clients[i].frameCountIncoming.front()){
                            Log log = *new Log(getTime(), message, frameCount_, hop, originalSenderID, originalReceiverID, 1, ActivityType::MESSAGE_RECEIVED);
                            clients[i].log_entries.push_back(log);
                            message.clear();
                            frameCount_ = 0;
                            clients[i].frameCountIncoming.pop();
                        }
                        clients[i].incoming_queue.pop();
                        continue;
                    }

                    int routeIndex = lookForRoute(originalReceiverID, clients, i);
                    if(routeIndex == -1){
                        if(frameCount_ == clients[i].frameCountIncoming.front()){
                            Log log = *new Log(getTime(), message, frameCount_, hop, originalSenderID, originalReceiverID, 0, ActivityType::MESSAGE_DROPPED);
                            clients[i].log_entries.push_back(log);
                            message.clear();
                            frameCount_ = 0;
                            clients[i].frameCountIncoming.pop();
                        }
                        clients[i].incoming_queue.pop();
                        continue;
                    }

                    int hopCount = dynamic_cast<PhysicalLayerPacket*> (clients[i].incoming_queue.front().top())->hopCount;
                    clients[i].incoming_queue.front().pop();
                    Packet* packet3 =  new Packet(3);
                    PhysicalLayerPacket* physLayer = dynamic_cast<PhysicalLayerPacket*> (packet3);
                    physLayer = new PhysicalLayerPacket(3, clients[i].client_mac, clients[routeIndex].client_mac);
                    physLayer->setHop(hopCount);
                    clients[i].incoming_queue.front().push(physLayer);
                    clients[i].outgoing_queue.push(clients[i].incoming_queue.front());
                    clients[i].incoming_queue.pop();

                    if(frameCount_ == clients[i].frameCountIncoming.front()){
                        Log log = *new Log(getTime(), message, frameCount_, hop, originalSenderID, originalReceiverID, 1, ActivityType::MESSAGE_FORWARDED);
                        clients[i].log_entries.push_back(log);
                        message.clear();
                        frameCount_ = 0;
                        clients[i].frameCountOutgoing.push(clients[i].frameCountIncoming.front());
                        clients[i].frameCountIncoming.pop();
                    }
                }
            }
        }
        else if(initialCommand == "PRINT_LOG"){
            string clientID = string(1, command[10]);
            int clientIndex = lookForClient(clientID, clients);

            cout << "Client " << clientID << " Logs:\n------------\n";

            for(int k = 0; k < clients[clientIndex].log_entries.size(); k++){
                Log log = clients[clientIndex].log_entries[k];
                string activityString;
                if(log.activity_type == ActivityType::MESSAGE_FORWARDED) activityString = "Message Forwarded";
                else if(log.activity_type == ActivityType::MESSAGE_DROPPED) activityString = "Message Dropped";
                else if(log.activity_type == ActivityType::MESSAGE_RECEIVED) activityString = "Message Received";
                else if(log.activity_type == ActivityType::MESSAGE_SENT) activityString = "Message Sent";

                string yesno = log.success_status ? "Yes" : "No";

                cout << "Log Entry #" << k + 1 << "\n";
                cout << "Activity: " << activityString << "\n";
                cout << "Timestamp: " << log.timestamp << "\n";
                cout << "Number of frames: " << log.number_of_frames << "\n";
                cout << "Number of hops: " << log.number_of_hops << "\n";
                cout << "Sender ID: " << log.sender_id << "\n";
                cout << "Receiver ID: " << log.receiver_id << "\n";
                cout << "Success: " << yesno << "\n";

                if(log.activity_type == ActivityType::MESSAGE_RECEIVED){
                    cout << "Message: \"" << log.message_content << "\"\n";
                }

                cout << "------------\n";

            }

        }
    }
}


string Network::getOriginialSenderID(stack<Packet*> frame){
    for(int i = 0; i < 3; i++){
        frame.pop();
    }
    string id = dynamic_cast<ApplicationLayerPacket*> (frame.top())->sender_ID;
    return id;
}

string Network::getOriginialReceiverID(stack<Packet*> frame){
    for(int i = 0; i < 3; i++){
        frame.pop();
    }
    string id = dynamic_cast<ApplicationLayerPacket*> (frame.top())->receiver_ID;
    return id;
}

string Network::getOriginialMessageChunk(stack<Packet*> frame){
    for(int i = 0; i < 3; i++){
        frame.pop();
    }
    string mes = dynamic_cast<ApplicationLayerPacket*> (frame.top())->message_data;
    return mes;
}

vector<Client> Network::read_clients(const string &filename) {
    vector<Client> clients;
    // TODO: Read clients from the given input file and return a vector of Client instances.
    std::ifstream file(filename);
    std::string line;
    std::getline(file, line);
    int clientsCount = stoi(line);

    for(int i = 0; i < clientsCount; i++){
        std::getline(file, line);
        std::string param;
        vector<std::string> params;
        for(char j : line){
            if(j != ' '){
                param += j;
            } else{
                params.push_back(param);
                param.clear();
            }
        }

        Client client = *new Client(params[0], params[1], param);
        clients.push_back(client);
    }
    return clients;
}

void Network::read_routing_tables(vector<Client> &clients, const string &filename) {
    // TODO: Read the routing tables from the given input file and populate the clients' routing_table member variable.
    std::ifstream file(filename);
    std::string line;
    int clientsCount = clients.size();

    for(int i = 0; i < clientsCount; i++){
        unordered_map < string, string > routing_table_;
        std::getline(file, line);
        if(line[0] == '-') std::getline(file, line);

        for(int j = 0; j < clientsCount - 1; j++){
            std::pair<string, string> pair = make_pair(string(1, line[0]), string(1,line[2]));
            routing_table_.insert(pair);
            std::getline(file, line);
        }

        clients[i].routing_table = routing_table_;
    }

}

// Returns a list of token lists for each command
vector<string> Network::read_commands(const string &filename) {
    vector<string> commands;
    // TODO: Read commands from the given input file and return them as a vector of strings.
    std::ifstream file(filename);
    std::string line;
    std::getline(file, line);

    while(std::getline(file, line)){
        commands.push_back(line);
    }
    return commands;
}

int Network::lookForClient(string adress, vector<Client> clients){
    int clientIndex = -1;
    for(int i = 0; i < clients.size(); i++){
        if(clients[i].client_mac == adress || clients[i].client_ip == adress || clients[i].client_id == adress) clientIndex = i;
    }
    return clientIndex;
}

int Network::lookForRoute(string receiverID, vector<Client> clients, int sendIndex){
    int routeIndex = -1;
    string routeID = clients[sendIndex].routing_table[receiverID];
    for(int k = 0; k < clients.size(); k++){
        if(clients[k].client_id == routeID){
            routeIndex = k;
        }
    }
    return routeIndex;
}

string Network::getTime(){
    time_t f = std::time(nullptr);
    std::tm* timeInfo = std::localtime(&f);
    char timeString[80];
    strftime(timeString, 80, "%Y-%m-%d %H:%M:%S", timeInfo);
    return timeString;
}

stack<Packet*> Network::createFrame(string senderID, string receiverID, string mes, string sender_port, string receiver_port,vector<Client> clients){
    int sendIndex = lookForClient(senderID, clients);
    int receiveIndex = lookForClient(receiverID, clients);
    int routeIndex = lookForRoute(receiverID, clients, sendIndex);

    stack<Packet*> frame = *new stack<Packet*>();

    Packet* packet0 =  new Packet(0);
    ApplicationLayerPacket* appLayer = dynamic_cast<ApplicationLayerPacket*> (packet0);

    Packet* packet1 =  new Packet(1);
    TransportLayerPacket* transLayer = dynamic_cast<TransportLayerPacket*> (packet1);

    Packet* packet2 =  new Packet(2);
    NetworkLayerPacket* netLayer = dynamic_cast<NetworkLayerPacket*> (packet2);

    Packet* packet3 =  new Packet(3);
    PhysicalLayerPacket* physLayer = dynamic_cast<PhysicalLayerPacket*> (packet3);

    appLayer = new ApplicationLayerPacket(0, senderID, receiverID, mes);
    transLayer = new TransportLayerPacket(1, sender_port, receiver_port);
    netLayer = new NetworkLayerPacket(2, clients[sendIndex].client_ip, clients[receiveIndex].client_ip);
    physLayer = new PhysicalLayerPacket(3, clients[sendIndex].client_mac, clients[routeIndex].client_mac);

    frame.push(appLayer);
    frame.push(transLayer);
    frame.push(netLayer);
    frame.push(physLayer);

    printStack(frame);

    return frame;
}

void Network::printStack(stack<Packet*> frame){
    string hop = dynamic_cast<PhysicalLayerPacket*> (frame.top())->printHop();
    for(int i = 0; i < 4; i++){
        frame.top()->print();
        frame.pop();
    }
    cout << hop;
}

int Network::getHop(stack<Packet*> frame){
    int hopCount;
    hopCount = dynamic_cast <PhysicalLayerPacket*> (frame.top())->hopCount;
    return hopCount;
}

Network::~Network() {
    // TODO: Free any dynamically allocated memory if necessary.


}
