//package carrotbat410.lol.controller.forStudy;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
////! (지표 확인용)트래픽 유발 컨트롤러
//@RestController
//@Slf4j
//@RequestMapping("/traffic")
//public class TrafficController {
//
//
//    @GetMapping("/cpu")
//    public String cpuStress() {
//        log.info("cpuStress");
//
//        long value = 0;
//        long value2 = 0;
//        for (long i = 0; i < 500000000000L; i++) {
//            value++;
//            value2++;
//        }
//        return "value= " + value;
//    }
//
//    private List<String> list = new ArrayList<>();
//    @GetMapping("/jvm")
//    public String jvmStress() {
//        log.info("jvmStress");
//
//        for (long i = 0; i < 500000000000L; i++) {
//            list.add("hello jvm! " + i);
//        }
//        return "ok";
//    }
//
//    @Autowired
//    DataSource dataSource;
//    @GetMapping("/jdbc")
//    public String jdbcConnectionStress() throws SQLException {
//        log.info("jdbcStress");
//
//        Connection conn1 = dataSource.getConnection();
//        log.info("conn1 info:", conn1);
//        Connection conn2 = dataSource.getConnection();
//        log.info("conn2 info:", conn2);
////        conn1.close(); 커넥션을 닫지 않는다.
////        conn2.close(); 커넥션을 닫지 않는다.
//        //사용중인 커넥션이 10개 넘어가면, time out
//        return "ok";
//    }
//
//    @GetMapping("/error-log")
//    public String errorLog() {
//        log.error("error log");
//        return "error";
//    }
//
//
//}
