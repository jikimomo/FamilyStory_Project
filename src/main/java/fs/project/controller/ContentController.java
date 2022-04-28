package fs.project.controller;

import fs.project.argumentresolver.Login;
import fs.project.domain.Content;
import fs.project.domain.Team;
import fs.project.domain.User;
import fs.project.service.TeamService;
import fs.project.service.UserService;
import fs.project.vo.ContentInputVO;
import fs.project.service.ContentService;
import fs.project.vo.ContentVO;
import fs.project.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final UserService userService;
    private final TeamService teamService;

    /* 게시물 업로드 */
    //uploadContentForm.html을 띄우는 컨트롤러 부분
    @GetMapping(value="/uploadContent")
    public String uploadForm(@Login User loginUser, Model model){
        User user = userService.findUser(loginUser.getUID());
        log.info("{}------------", user.getUID());
        Long curTID;
        if(user.getCurTid() == null){
            curTID = 0L;
        }else{
            curTID = user.getCurTid();
        }

        Team team = teamService.findTeam(curTID);

        model.addAttribute("curTID", curTID);
        model.addAttribute("user", user);
        model.addAttribute("teamID", team.getTeamID());

        return "content/uploadContentForm";
    }

    //uploadContentForm.html에서 보낸 content 관련 데이터들을 저장하는 컨트롤러 부분
    @PostMapping(value="/uploadContent/{tID}")
    public String upload(@Login User loginUser,
                         @PathVariable("tID") Long tID,
                         @RequestParam("photoRoute") List<MultipartFile> images,
                         @RequestParam("explanation") String explanation,
                         @RequestParam("location") String location,
                         @RequestParam("when") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate when,
                         Model model) throws Exception {

        //현재 사용자 및 팀의 정보
        User user = userService.findUser(loginUser.getUID());
        Team team = teamService.findTeam(tID);

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
            return "redirect:/loadMainPage/"+tID;
        }
    }

    /* 개인페이지! */
    //uploadList.html을 띄우는 컨트롤러 부분
    @GetMapping(value="/uploadList/{tID}/{uID}")
    public String uploadList(@Login User loginUser, @PathVariable("tID") Long tID, @PathVariable("uID") Long uID, Model model){
        //현재 사용자 정보
        User user = userService.findUser(loginUser.getUID());
//
//        //현재 그룹과 어떤 구성원인지에 관한 정보
//        User userInSameTeam = contentService.findUser(uID);
//        Team team = contentService.findTeam(tID);
//
//        //content
//        List<Content> contentOriginal = new ArrayList<>();
//        if(loginUser.getUID() == uID) {
//            contentOriginal = contentService.findAllByUT(user.getUID(), team.getTID());
//        }
//        else{
//            contentOriginal = contentService.findAllByUT(userInSameTeam.getUID(), team.getTID());
//        }
//
//        model.addAttribute("user", user);
//        model.addAttribute("userInSameTeam", userInSameTeam);
//        model.addAttribute("tID", team.getTID());
//        model.addAttribute("contents", contentOriginal);
        Long curTID;
        if(user.getCurTid() == null){
            curTID = 0L;
        }else{
            curTID = user.getCurTid();
        }
        model.addAttribute("curTID", curTID);

        return "content/personalPage";
    }

    //로그인 정보
    @ResponseBody
    @GetMapping(value="/uploadList/loginUser")
    public UserVO initLoginUser(@Login User loginUser){
        Long uid = loginUser.getUID();
        User u = userService.findUser(uid);

        UserVO userVO = new UserVO();
        userVO.setUID(u.getUID());
        userVO.setUserID(u.getUserID());
        userVO.setName(u.getName());
        userVO.setNickName(u.getNickName());
        userVO.setBirthday(u.getBirthday());
        userVO.setUserImage(u.getUserImage());
        userVO.setCoverImage(u.getCoverImage());
        userVO.setMainTid(u.getMainTid());

        return userVO;
    }

    //페이지 유저 정보
    @ResponseBody
    @GetMapping(value="/uploadList/pageUser")
    public UserVO initPageUser(@RequestParam String uID){
        User u = userService.findUser(Long.parseLong(uID));

        UserVO userVO = new UserVO();
        userVO.setUID(u.getUID());
        userVO.setUserID(u.getUserID());
        userVO.setName(u.getName());
        userVO.setNickName(u.getNickName());
        userVO.setBirthday(u.getBirthday());
        userVO.setUserImage(u.getUserImage());
        userVO.setCoverImage(u.getCoverImage());
        userVO.setMainTid(u.getMainTid());

        return userVO;
    }

    //컨텐츠 정보
    @ResponseBody
    @PostMapping(value="/uploadList/contents")
    public List<ContentVO> initContents(@Login User loginUser, @RequestParam String tID, @RequestParam String uID){
        List<Content> contents = contentService.findAllByUT(Long.parseLong(uID), Long.parseLong(tID));
        List<ContentVO> contentVO = new ArrayList<>();

        User pageUser = userService.findUser(Long.parseLong(uID));

        for(Content c : contents){
            ContentVO cVO = new ContentVO();
            cVO.setCID(c.getCID());
            cVO.setExplanation(c.getExplanation());
            cVO.setLocation(c.getLocation());
            cVO.setWhen(c.getWhen());
            cVO.setPhotoRoute(c.getPhotoRoute());
            cVO.setUploadTime(c.getUploadTime());
            cVO.setUserNickname(pageUser.getNickName());
            cVO.setUserImage(pageUser.getUserImage());
            contentVO.add(cVO);
        }

        return contentVO;
    }

    /* 게시물 수정 */
    //changeContentForm.html을 화면에 띄우는 컨트롤러 부분
    @PostMapping(value="/uploadList/{cID}/changeForm")
    public String changeContentForm(@Login User loginUser, @PathVariable("cID") Long cID, Model model){
        Content content = contentService.findOne(cID);

        User user = userService.findUser(loginUser.getUID());
        Team team = teamService.findTeam(user.getMainTid());

        Long curTID;
        if(user.getCurTid() == null){
            curTID = 0L;
        }else{
            curTID = user.getCurTid();
        }
        model.addAttribute("curTID", curTID);

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

        Content contentChanged = contentService.findOne(cID);
        Long tID = contentChanged.getTeam().getTID();
        Long uID = contentChanged.getUser().getUID();

        return "redirect:/uploadList/"+tID+"/"+uID;
    }

    /* 게시물 삭제 */
    //cID에 해당하는 content를 삭제하는 컨트롤러 부분
    @PostMapping(value="/uploadList/{cID}/delete")
    public String deleteContent(@PathVariable("cID") Long cID){
        Content contentDeleted = contentService.findOne(cID);
        Long tID = contentDeleted.getTeam().getTID();
        Long uID = contentDeleted.getUser().getUID();

        contentService.deleteContent(cID);

        return "redirect:/uploadList/"+tID+"/"+uID;
    }

    //1년 전에 올렸던 사진 보여주기
    @ResponseBody
    @PostMapping("/agoYear")
    public String agoYear (@Login User loginUser,  @RequestParam String tID){

        Long tId = Long.parseLong(tID);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String s = sdf.format(now); //형식 변환
        String y = s.substring(0,4);
        log.info("{}", y);
        String x = s.substring(4);
        int y1 = Integer.parseInt(y);
       // y1--;
        String y2 = Integer.toString(y1);
        String when1 = y2+x;
        LocalDate when = LocalDate.parse(when1, DateTimeFormatter.ISO_DATE);//local_date로 변환
        List<String> photoRoute = contentService.findTid(when, tId); //team_event에서 오늘날짜와 같은 tid값을 받아온다.

        for( String pr : photoRoute){
            log.info("photoRoute {}", pr);
        }

        if(photoRoute.isEmpty()){
            log.info("비었음");
            return "";
        }
        else {
            log.info("사진 경로 : {}", photoRoute.get(0));
            return photoRoute.get(0);
        }
    }
}
