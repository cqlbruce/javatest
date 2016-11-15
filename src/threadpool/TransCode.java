package threadpool;

public enum TransCode {
    //业务可以扩展
    BALANCE("balance", "余额查询"), PAY("pay", "支付"), QUERY("query", "同步支付结果"), DETAIL("detail", "交易明细查询"), CHECK("check", "对账");

    private String code;
    private String desc;

    private TransCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String toString() {
        return ":::" + code + ":::(" + desc + ")";
    }
}
