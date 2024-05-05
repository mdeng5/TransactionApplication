package controller.integrationtest;

public enum Action {
    LOAD,
    AUTHORIZATION;

    public static Action fromString(String value) {
        return Action.valueOf(value.toUpperCase());
    }
}
