package io.github.splotycode.mosaik.netty.packet.serialized;

import io.github.splotycode.mosaik.netty.packet.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SerializedPacketEncoder extends MessageToByteEncoder<SerializedPacket> {

	private PacketRegistry<SerializedPacket> registry;

	@Override
	protected void encode(ChannelHandlerContext ctx, SerializedPacket packet, ByteBuf output) throws Exception {
		int id = registry.getIdByPacket(packet.getClass());
		if(id == -1) {
			throw new NullPointerException("Coud not find id to packet: " + packet.getClass().getSimpleName());
		}
		PacketSerializer ps = new PacketSerializer(output);
		ps.writeVarInt(id);
		packet.write(ps);
	}

}
