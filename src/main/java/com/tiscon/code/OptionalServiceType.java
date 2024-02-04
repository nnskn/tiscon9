package com.tiscon.code;

/**
 * オプションサービスのコード
 *
 * @author Oikawa Yumi
 */
public enum OptionalServiceType implements CodeEnum {

    /** 洗濯機取り付け */
    WASHING_MACHINE(1, "洗濯機取り付け"), BOX_COLLECT(2,"段ボール回収"), 
    NEW_LIFE_SET(3,"新生活セット"),FURNITURE_SETTING(4,"家具配置サービス"),
    PUBLIC_FEE(5,"公共料金サービス");

    // BOX_COLLECT(2, "段ボール回収");

    /** オプションサービスのラベル */
    private final String label;
    /** オプションサービスのラベルのコード */
    private final int code;

    /**
     * コンストラクタ。
     *
     * @param code  コード値
     * @param label ラベル
     */
    OptionalServiceType(int code, String label) {
        this.label = label;
        this.code = code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public int getCode() {
        return code;
    }
}
