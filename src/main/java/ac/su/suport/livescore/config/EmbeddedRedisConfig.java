package ac.su.suport.livescore.config;//package ac.su.suport.livescore.config;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.io.ClassPathResource;
//import redis.embedded.RedisExecProvider;
//import redis.embedded.RedisServer;
//import redis.embedded.util.OS;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Objects;
//
///**
// * 로컬 환경일경우 내장 레디스가 실행됩니다.
// */
//@Profile("local")
//@Configuration
//public class EmbeddedRedisConfig {
//
//    @Value("${spring.data.redis.port}")
//    private int redisPort;
//
//    private RedisServer redisServer;
//
//    @PostConstruct
//    public void redisServer() {
//        try {
//            if (isArmMac()) {
//                redisServer = RedisServer.builder()
//                        .port(redisPort)
//                        .setting("maxmemory 128M")
//                        .redisExecProvider(customRedisExec())
//                        .build();
//            } else {
//                redisServer = RedisServer.builder()
//                        .port(redisPort)
//                        .setting("maxmemory 128M")
//                        .build();
//            }
//            System.out.println("Starting Redis Server...");
//            redisServer.start();
//            System.out.println("Redis Server started.");
//        } catch (Exception e) {
//            System.err.println("Failed to start Redis server: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @PreDestroy
//    public void stopRedis() {
//        if (redisServer != null) {
//            redisServer.stop();
//        }
//    }
//
//    private boolean isArmMac() {
//        return Objects.equals(System.getProperty("os.arch"), "aarch64")
//                && Objects.equals(System.getProperty("os.name"), "Mac OS X");
//    }
//
//    private RedisExecProvider customRedisExec() {
//        try {
//            File redisFile = getRedisFileForArcMac();
//            if (redisFile == null || !redisFile.exists()) {
//                throw new RuntimeException("Redis binary file does not exist: " + redisFile);
//            }
//            return RedisExecProvider.defaultProvider().override(OS.UNIX, redisFile.getAbsolutePath());
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to get Redis binary file for ARM Mac", e);
//        }
//    }
//
//    private File getRedisFileForArcMac() {
//        try {
//            File redisFile = new ClassPathResource("binary/redis/redis-server-7.2.3-mac-arm64").getFile();
//            if (!redisFile.setExecutable(true)) {
//                throw new RuntimeException("Failed to set executable permission on Redis binary");
//            }
//            return redisFile;
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to get Redis binary file for ARM Mac", e);
//        }
//    }
//}