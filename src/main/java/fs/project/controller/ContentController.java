package fs.project.controller;

import fs.project.domain.Content;
import fs.project.domain.Team;
import fs.project.domain.User;
import fs.project.form.ContentInputVO;
import fs.project.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    //uploadContentForm.html을 띄우는 컨트롤러 부분
    @GetMapping(value="/uploadContent")
    public String uploadForm(Model model){
        User user = contentService.findUser(1L);
        Team team = contentService.findTeam(1L);

        model.addAttribute("user", user);
        model.addAttribute("team", team);



        return "content/uploadContentForm";
    }

    //uploadContentForm.html에서 보낸 content 관련 데이터들을 저장하는 컨트롤러 부분
    @PostMapping(value="/uploadContent")
    public String upload(@RequestParam("photoRoute") List<MultipartFile> images,
                         @RequestParam("explanation") String explanation,
                         @RequestParam("location") String location,
                         @RequestParam("when") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate when,
                         Model model) throws Exception {

        //현재 사용자 및 팀의 정보
        User user = contentService.findUser(1L);
        Team team = contentService.findTeam(1L);

        //사진 경로 관련 코드
        String photoRoute = contentService.filePath(images);
        if(photoRoute.equals("")){ //이미지 파일이 하나도 선택 안된 경우
            model.addAttribute("error message", "이미지 파일이 선택되지 않았습니다.");
            return "redirect:/upload";
        }
        else {
            //contentForm 객체 생성
            ContentInputVO contentInput = new ContentInputVO(photoRoute, explanation, location, when);

            //content 저장하기
            contentService.uploadContent(user.getUID(), team.getTID(), contentInput);
            return "redirect:/uploadList";
        }
    }

    //uploadList.html을 띄우는 컨트롤러 부분
    @GetMapping(value="/uploadList")
    public String uploadList(Model model){
        //현재 사용자 및 팀의 정보
        User user = contentService.findUser(1L);
        Team team = contentService.findTeam(1L);

        //content
        List<Content> contentOriginal = contentService.findAllByUT(user.getUID(), team.getTID());

        model.addAttribute("user", user);
        model.addAttribute("team", team);
        model.addAttribute("contents", contentOriginal);

        return "content/uploadList";
    }

    //changeContentForm.html을 화면에 띄우는 컨트롤러 부분
    @PostMapping(value="/uploadList/{cID}/changeForm")
    public String changeContentForm(@PathVariable("cID") Long cID, Model model){
        Content content = contentService.findOne(cID);
        Team team = contentService.findTeam(1L);

        model.addAttribute("content", content);
        model.addAttribute("team", team);
        return "content/changeContentForm";
    }

    //changeContentForm.html에서 보낸 content 변경 값을 update하는 컨트롤러 부분
    @PostMapping(value="/uploadList/{cID}/change")
    public String changeContent(@PathVariable("cID") Long cID,
                                @RequestParam("photoRoute") List<MultipartFile> images,
                                @RequestParam("explanation") String explanation,
                                @RequestParam("location") String location,
                                @RequestParam("when") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate when) throws Exception{

        String photoRoute = contentService.filePath(images);
        if(photoRoute.equals("")){
            Content contentOriginal = contentService.findOne(cID);
            ContentInputVO contentInput = new ContentInputVO(contentOriginal.getPhotoRoute(), explanation, location, when);
            contentService.updateContent(cID, contentInput);
        }
        else {
            ContentInputVO contentInput = new ContentInputVO(photoRoute, explanation, location, when);
            contentService.updateContent(cID, contentInput);
        }

        return "redirect:/uploadList";
    }

    //cID에 해당하는 content를 삭제하는 컨트롤러 부분
    @PostMapping(value="/uploadList/{cID}/delete")
    public String deleteContent(@PathVariable("cID") Long cID){
        contentService.deleteContent(cID);
        return "redirect:/uploadList";
    }




    //1년 전에 올렸던 사진 보여주기
    @ResponseBody
    @GetMapping("/{tid}/content")
    public void event(@PathVariable Long tid, Model model){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String s = sdf.format(now); //형식 변환
        String y = s.substring(0,4);
        log.info("{}", y);
        String x = s.substring(4);
        int y1 = Integer.parseInt(y);
        y1--;
        String y2 = Integer.toString(y1);
        String when1 = y2+x;
        LocalDate when = LocalDate.parse(when1, DateTimeFormatter.ISO_DATE);//local_date로 변환
        List<String> photoRoute = contentService.findTid(when, tid); //team_event에서 오늘날짜와 같은 tid값을 받아온다.

        if(photoRoute.isEmpty()){
            model.addAttribute("photoRoute", null);
        }
        for( String pr : photoRoute){
            model.addAttribute("photoRoute", pr);
            log.info(pr);
            break;
        }
    }
}
