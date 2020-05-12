package net.portfo.enums;

/**
 * Created by chesterjavier on 5/12/20.
 */
public enum RoleEnum {

    ROLE_ADMIN(1);

    private Integer code;

    RoleEnum(final Integer code) {
        this.code = code;
    }

    public String getRole() {
        return toString();
    }
}
