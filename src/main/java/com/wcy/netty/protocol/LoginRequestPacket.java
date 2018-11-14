package com.wcy.netty.protocol;


import lombok.Data;

import static com.wcy.netty.protocol.Command.lOGIN_REQUEST;

@Data
public class LoginRequestPacket extends Packet{

    private Integer userId;

    private String username;

    private String password;

    @Override
    public Byte getCommand() {
        return lOGIN_REQUEST;
    }
}
