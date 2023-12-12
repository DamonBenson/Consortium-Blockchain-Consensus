package pojo.msg;

public enum MsgType {

    /**
     * 密钥协商阶段client向server发送的消息类型
     */
    CONN,
    /**
     * 密钥协商阶段server收到client的CONN消息后，返回的消息类型
     */
    CONN_REPLY,
    /**
     * 用户发送请求到P2P网络的消息类型
     */
    REQ,
    /**
     * Honey Badger共识的VAL消息
     */
    VAL,
    /**
     * Honey Badger共识的ECHO消息
     */
    ECHO,
    /**
     * Honey Badger共识的READY消息
     */
    READY,
    /**
     * Honey Badger共识的BVAL消息
     */
    BVAL,
    /**
     * Honey Badger共识的AUX消息
     */
    AUX,
    /**
     * 论文共识的PREPARE消息
     */
    X_PREPARE,
    /**
     * 论文共识的PREPARE_VOTE消息
     */
    X_PREPARE_VOTE,
    /**
     * 论文共识的COMMIT消息
     */
    X_COMMIT,
    /**
     * 论文共识的BVAL消息，与Honey Badger中相同
     */
    X_BVAL,
    /**
     * 论文共识的AUX消息，与Honey Badger中相同
     */
    X_AUX,
    /**
     * PBFT共识的PRE_PREPARE消息
     */
    PRE_PREPARE,
    /**
     * PBFT共识的PREPARE消息
     */
    PREPARE,
    /**
     * PBFT共识的COMMIT消息
     */
    COMMIT;


    private static MsgType[] int2EnumMap = null;

    /**
     * 枚举类本身不提供索引到对象的映射，需要自行实现；另外，对象到索引的映射可以使用枚举类自带的ordinal()函数
     * @param i 枚举对象索引
     * @return 枚举对象
     */
    public static MsgType int2Enum(int i) {
        if(int2EnumMap == null) {
            MsgType.int2EnumMap = MsgType.values();
        }
        return MsgType.int2EnumMap[i];
    }

}
