package ch.yax.connect.quickstart.source;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class RandomSourceConfig extends AbstractConfig {

    public static final String TASK_ID = "task.id";
    public static final String TASK_MAX = "task.max";

    public static final String TOPIC_NAME_CONFIG = "topic";
    public static final String POLL_INTERVAL_CONFIG = "poll.interval.ms";
    public static final long POLL_INTERVAL_DEFAULT = 1000;

    public static final String WS_URI = "ws.uri";
    public static final String RECONNECT_NO_RECORD = "reconnect.no.record";
    public static final String INACTIVITY_MS = "inactivity.ms";
    public static final String RECONNECT_DELAY = "reconnect.delay";

    public static final ConfigDef CONFIG_DEF =
            new ConfigDef()
                    .define(TOPIC_NAME_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Topic name.")
                    .define(WS_URI, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "URI fo the Websocket. e.g. ws://192.168.1.10:8181")
                    .define(RECONNECT_NO_RECORD, ConfigDef.Type.BOOLEAN, false, ConfigDef.Importance.MEDIUM, "reconnect WS connection if there are no record for certain interval as configured on inactivity.ms")
                    .define(RECONNECT_DELAY, ConfigDef.Type.LONG, 10000, ConfigDef.Importance.HIGH, "backoff ms before reconnection")
                    .define(INACTIVITY_MS, ConfigDef.Type.LONG, 300000, ConfigDef.Importance.HIGH, "inactivity time in ms before reconnect")
                    .define(POLL_INTERVAL_CONFIG, ConfigDef.Type.LONG, POLL_INTERVAL_DEFAULT, ConfigDef.Importance.HIGH, "Max interval between messages (ms)");


    public RandomSourceConfig(final Map<?, ?> props) {
        super(CONFIG_DEF, props);
    }

    public String getTopicName() {
        return getString(TOPIC_NAME_CONFIG);
    }

    public long getPollInterval() {
        return getLong(POLL_INTERVAL_CONFIG);
    }

}
