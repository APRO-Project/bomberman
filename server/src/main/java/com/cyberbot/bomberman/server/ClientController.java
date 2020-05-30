package com.cyberbot.bomberman.server;

import com.cyberbot.bomberman.core.models.net.packets.ClientRegisterRequest;
import com.cyberbot.bomberman.core.models.net.packets.ClientRegisterResponse;

public interface ClientController {
    ClientRegisterResponse onClientRegister(ClientRegisterRequest request, ClientControlService service);
}
