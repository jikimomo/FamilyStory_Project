package fs.project.controller;
import fs.project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final UserService userService;

    String api_key = " NCSO4XDT7CHQHZVD";  //api_key 유출되면 안됩니다.
    String api_secret = "WDBIGEVM6PYAMSDV3H27YMEAVQST8RXQ";  //api_secret 유출되면 안됩니다.
    @Scheduled(cron = "0 0 0 * * *") //매일 00시 00분 00초에 메세지 발송
    public void cronJobSch() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now); //형식 변환
        String event = strDate.substring(0,10); //yyyy-mm-dd 형식으로 문자열 자르기
        LocalDate date = LocalDate.parse(event, DateTimeFormatter.ISO_DATE);//local_date로 변환
        List<Long> tid = userService.findTid(date); //team_event에서 오늘날짜와 같은 tid값을 받아온다.
        for (Long t : tid){

            //team_event에서 tid값과 같은 event 내용들을 붙여서 받아온다.
            String e = userService.findEvent(t);
            //user_team 테이블에서 tid에 속한 유저의 휴대폰 번호를 list형태로 받아온다.
            List<String> phoneNumber = userService.findPhoneNumber(t);

            for(String pn : phoneNumber){

                Message coolsms = new Message(api_key, api_secret);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("to", pn); //수신자 폰번호
                params.put("from", "01096403007");//발신자 폰번호
                params.put("type", "SMS");
                params.put("text", e); //보낼 문구
                params.put("app_version", "test app 1.2");

//              주의!!!!!! 절대 주석 풀지 마세요 !!!!!!!!!!!

                /*
                try {
                    JSONObject obj = (JSONObject) coolsms.send(params);
                    System.out.println(obj.toString());
                } catch (CoolsmsException ex) {
                    System.out.println(ex.getMessage());
                    System.out.println(ex.getCode());
                }
                */
            }
        }
    }
}

