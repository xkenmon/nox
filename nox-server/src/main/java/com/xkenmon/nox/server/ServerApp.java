package com.xkenmon.nox.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.beust.jcommander.Parameter;
import com.xkenmon.nox.common.BaseApp;
import com.xkenmon.nox.common.util.StringUtil;
import com.xkenmon.nox.server.ServerApp.CommandArgs;
import com.xkenmon.nox.server.configuration.ServerConfiguration;
import com.xkenmon.nox.server.configuration.ServerConfiguration.PortPasswordPair;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import lombok.Data;

public class ServerApp extends BaseApp<CommandArgs> {

  private static final String defaultConfigFileName = "nox-server-config.json";

  @Override
  protected CommandArgs createCommandObject() {
    return new CommandArgs();
  }

  @Override
  protected void run(CommandArgs commandArgs) {
    ServerConfiguration configuration = new ServerConfiguration();
    try {
      if (commandArgs.configPath == null) {
        var defaultConfigFile = defaultConfigFile();
        if (defaultConfigFile.exists()) {
          configuration = parseJson(defaultConfigFile);
        }
      } else {
        var configPath = Paths.get(commandArgs.configPath);
        var file = configPath.toFile();
        configuration = parseJson(file);
      }
    } catch (IOException e) {
      System.out.printf("can't read config file: %s", defaultConfigFile().getAbsolutePath());
      System.exit(1);
    } catch (JSONException e) {
      System.out.printf("can not parse json file: %s", defaultConfigFile().getAbsolutePath());
      System.exit(1);
    }

    rewriteConfiguration(commandArgs, configuration);

    setLogLevel(convertLogLevel(configuration.getDebug()));

    NoxServerBootstrap bootstrap = new NoxServerBootstrap(configuration);
    bootstrap.start();
  }

  private static void rewriteConfiguration(CommandArgs args, ServerConfiguration configuration) {
    if (StringUtil.notBlank(args.getListenAddress())) {
      configuration.setListenAddr(args.getListenAddress());
    }
    if (StringUtil.notBlank(args.getMethod())) {
      configuration.setMethod(args.getMethod());
    }
    if (StringUtil.notBlank(args.getPassword()) && args.getPort() != null) {
      if (null == configuration.getPortPasswordPairs()) {
        configuration.setPortPasswordPairs(new HashSet<>());
      }
      configuration.getPortPasswordPairs()
          .add(new PortPasswordPair(args.getPort(), args.getPassword()));
    }
    if (null != args.getTimeout()) {
      configuration.setTimeoutInSecond(args.getTimeout());
    }
    if (null != args.getDebug()) {
      configuration.setDebug(args.getDebug());
    }
  }

  private static File defaultConfigFile() {
    var home = System.getProperty("user.home");
    return Paths.get(home, defaultConfigFileName).toFile();
  }

  private static ServerConfiguration parseJson(File jsonFile) throws IOException {
    var json = Files.readString(jsonFile.toPath());
    return JSON.parseObject(json, ServerConfiguration.class);
  }

  @Data
  static class CommandArgs {

    @Parameter(names = {"-c",
        "--config"}, description = "special config file path (json format).")
    private String configPath;

    @Parameter(names = {"-l", "--listen"}, description = "local listen address")
    private String listenAddress;

    @Parameter(names = {"-p", "--port"}, description = "server port")
    private Integer port;

    @Parameter(names = {"-k", "--password"}, description = "password")
    private String password;

    @Parameter(names = {"-m", "--method"}, description = "encrytion method")
    private String method;

    @Parameter(names = {"-t", "--timeout"}, description = "timeout in second")
    private Integer timeout;

    @Parameter(names = {
        "--debug"}, description = "debug log level. [0: debug, 1: info, 2: warn, 3: error]")
    private Integer debug = 1;

    @Parameter(names = {"-h", "--help"}, description = "show usage", help = true)
    private String help;

  }
}
