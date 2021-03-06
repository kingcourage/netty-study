package com.wcy.netty.protocol.request;

import com.wcy.netty.protocol.Packet;
import lombok.Data;

import java.util.List;

import static com.wcy.netty.protocol.command.Command.CREATE_GROUP_REQUEST;

@Data
public class CreateGroupRequestPacket extends Packet {

    private List<String> userIdList;
    @Override
    public Byte getCommand() {
        return CREATE_GROUP_REQUEST;
    }
}
