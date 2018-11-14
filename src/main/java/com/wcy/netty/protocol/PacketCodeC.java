package com.wcy.netty.protocol;

import com.wcy.netty.serialize.JSONSerializer;
import com.wcy.netty.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.HashMap;
import java.util.Map;

import static com.wcy.netty.protocol.Command.lOGIN_REQUEST;

public class PacketCodeC {
    private static final int MAGIC_NUMBER = 0x12345678;
    private static final Map<Byte,Class<? extends Packet>> packetTypeMap;
    private static final Map<Byte,Serializer> serializerMap;

    static {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(lOGIN_REQUEST,LoginRequestPacket.class);

        serializerMap = new HashMap<>();
        Serializer serializer = new JSONSerializer();
        serializerMap.put(serializer.getSerializerAlgorithm(),serializer);
    }

    public ByteBuf encode(Packet packet){
        //创建ByteBuf对象
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();

        //序列化java对象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        //实际编码过程
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }


    public Packet decode(ByteBuf byteBuf){
        //跳过magic number
        byteBuf.skipBytes(4);

        //跳过版本号
        byteBuf.skipBytes(1);

        //序列化算法标识
        byte serializeAlgorithm = byteBuf.readByte();

        //指令
        byte commond = byteBuf.readByte();

        //数据包长度
        int lenth = byteBuf.readInt();

        byte[] bytes = new byte[lenth];
        byteBuf.readBytes(bytes);

        Class<? extends Packet> requestType = getRequestType(commond);

        Serializer serializer = getSerializer(serializeAlgorithm);

        if(requestType != null && serializer != null){
            return serializer.deserialize(requestType,bytes);
        }
        return null;
    }

    private Serializer getSerializer(byte serializerAlgorithm){
        return  serializerMap.get(serializerAlgorithm);
    }

    private Class<? extends Packet> getRequestType(byte commond){
        return packetTypeMap.get(commond);
    }
}
