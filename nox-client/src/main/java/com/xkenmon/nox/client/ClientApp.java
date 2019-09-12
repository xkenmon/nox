package com.xkenmon.nox.client;

import com.beust.jcommander.Parameter;
import com.xkenmon.nox.client.ClientApp.CommandArgs;
import com.xkenmon.nox.client.configuration.ClientConfiguration;
import com.xkenmon.nox.common.BaseApp;
import lombok.Data;

public class ClientApp extends BaseApp<CommandArgs> {

  @Override
  protected CommandArgs createCommandObject() {
    return new CommandArgs();
  }

  @Override
  protected void run(CommandArgs commandArgs) {
    ClientConfiguration configuration = covertToClientConfiguration(commandArgs);

    setLogLevel(convertLogLevel(commandArgs.getDebug()));

    // boot client
    NoxClientBootstrap bootstrap = new NoxClientBootstrap(configuration);
    bootstrap.start();
  }

  private static ClientConfiguration covertToClientConfiguration(CommandArgs args) {
    return ClientConfiguration.builder()
        .localAddr(args.getLocalAddress())
        .localPort(args.getLocalPort())
        .method(args.getMethod())
        .password(args.getPassword())
        .remoteAddr(args.getRemoteAddress())
        .remotePort(args.getRemotePort())
        .timeout(args.getTimeout())
        .fastOpen(args.getFastOpen())
        .build();
  }


  @Data
  static class CommandArgs {

    @Parameter(names = {"-l", "--listen"}, description = "local listen port")
    private Integer localPort = 11080;

    @Parameter(names = {"-b", "--bind"}, description = "local binding address")
    private String localAddress = "127.0.0.1";

    @Parameter(names = {"-p", "--port"}, description = "server port", required = true)
    private Integer remotePort;

    @Parameter(names = {"-s", "--server"}, description = "server address", required = true)
    private String remoteAddress;

    @Parameter(names = {"-k", "--password"}, description = "password", required = true)
    private String password;

    @Parameter(names = {"-m", "--method"}, description = "encrytion method")
    private String method = "aes-256-cfb";

    @Parameter(names = {"-t", "--timeout"}, description = "timeout in second")
    private Integer timeout = 5 * 60;

    @Parameter(names = {"--fast-open"}, description = "enable client TFO feature")
    private Boolean fastOpen = false;

    @Parameter(names = {
        "--debug"}, description = "debug log level. [0: debug, 1: info, 2: warn, 3: error]")
    private Integer debug = 1;

    @Parameter(names = {"-h", "--help"}, description = "show usage", help = true)
    private String help;

  }
}
