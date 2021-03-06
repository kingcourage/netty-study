package com.wcy.netty.protocol.response;

import com.wcy.netty.protocol.Packet;
import lombok.Data;

import java.util.List;

import static com.wcy.netty.protocol.command.Command.CREATE_GROUP_RESPONSE;

@Data
public class CreateGroupResponsePacket extends Packet {
    private boolean success;

    private String groupId;

    private List<String> userNameList;

    @Override
    public Byte getCommand() {
        return CREATE_GROUP_RESPONSE;
    }
}
