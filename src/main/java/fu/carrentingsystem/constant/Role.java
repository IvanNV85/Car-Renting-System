package fu.carrentingsystem.constant;

public enum Role {
    ADMIN,
    CUSTOMER;

    public static Role fromString(String roleStr) {
        if (roleStr == null) return CUSTOMER;
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(roleStr)) {
                return r;
            }
        }
        return CUSTOMER;
    }
}
