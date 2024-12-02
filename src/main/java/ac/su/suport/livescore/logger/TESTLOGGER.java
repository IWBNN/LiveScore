package ac.su.suport.livescore.logger;

import ac.su.suport.livescore.controller.MatchController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TESTLOGGER {
    private static final Logger logger = LoggerFactory.getLogger(AdminLogger.class.getName());

    public static void logRequest() {
        logger.info("TEST LOGGER");
        System.out.println("테스트로거 호출됨");
    }
}