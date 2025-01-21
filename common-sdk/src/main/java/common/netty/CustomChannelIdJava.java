package common.netty;

import common.snowflake.SnowFlakeSeq;
import io.netty.channel.ChannelId;
import org.jetbrains.annotations.NotNull;

public final class CustomChannelIdJava implements ChannelId {
    private static final long serialVersionUID = 1096867517162468422L;

    public static CustomChannelIdJava newInstance() {
        return new CustomChannelIdJava();
    }


    private static SnowFlakeSeq snowFlakeSeq = new SnowFlakeSeq();
    private long value;

    private CustomChannelIdJava() {
        value = snowFlakeSeq.nextId();
    }

    @Override
    public String asShortText() {
        return String.valueOf(value);
    }

    @Override
    public String asLongText() {
        return String.valueOf(value);
    }

    @Override
    public int compareTo(@NotNull ChannelId o) {
        return o instanceof CustomChannelIdJava ? 0 : this.asLongText().compareTo(o.asLongText());
    }

    public static CustomChannelIdJava fromLongText(String text) {
        CustomChannelIdJava id = new CustomChannelIdJava();
        id.value = Long.parseLong(text);
        return id;
    }
}
