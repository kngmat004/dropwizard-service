package knightinc;

import javax.validation.constraints.NotNull;

public class databaseConfig {

    @NotNull
    String name;

    @NotNull
    String port;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
