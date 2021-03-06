package io.github.splotycode.mosaik.networking.packet.system;

import io.github.splotycode.mosaik.networking.packet.PacketRegistry;
import io.github.splotycode.mosaik.networking.packet.buf.BufPacket;
import io.github.splotycode.mosaik.networking.packet.buf.BufPacketDecoder;
import io.github.splotycode.mosaik.networking.packet.buf.BufPacketEncoder;
import io.github.splotycode.mosaik.networking.packet.gson.GsonPacket;
import io.github.splotycode.mosaik.networking.packet.gson.GsonPacketDecoder;
import io.github.splotycode.mosaik.networking.packet.gson.GsonPacketEncoder;
import io.github.splotycode.mosaik.networking.packet.serialized.SerializedPacket;
import io.github.splotycode.mosaik.networking.packet.serialized.SerializedPacketDecoder;
import io.github.splotycode.mosaik.networking.packet.serialized.SerializedPacketEncoder;
import io.netty.channel.ChannelHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultPacketSystem {

   public static PacketSystem<BufPacket> createBuf(PacketRegistry<BufPacket> registry) {
       return new SimplePacketSystem<BufPacket>(registry) {
           @Override
           ChannelHandler decoder() {
               return new BufPacketDecoder(registry);
           }

           @Override
           ChannelHandler encoder() {
               return new BufPacketEncoder(registry);
           }
       };
   }

    public static PacketSystem<GsonPacket> createGSON(PacketRegistry<GsonPacket> registry) {
        return new SimplePacketSystem<GsonPacket>(registry) {
            @Override
            ChannelHandler decoder() {
                return new GsonPacketDecoder(registry);
            }

            @Override
            ChannelHandler encoder() {
                return new GsonPacketEncoder(registry);
            }
        };
    }

    public static PacketSystem<SerializedPacket> createSerialized(PacketRegistry<SerializedPacket> registry) {
        return new SimplePacketSystem<SerializedPacket>(registry) {
            @Override
            ChannelHandler decoder() {
                return new SerializedPacketDecoder(registry);
            }

            @Override
            ChannelHandler encoder() {
                return new SerializedPacketEncoder(registry);
            }
        };
    }

}
