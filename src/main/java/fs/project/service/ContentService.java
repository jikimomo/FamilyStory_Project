package fs.project.service;

import fs.project.domain.*;
import fs.project.repository.ContentRepository;
import fs.project.form.ContentInputVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    /* user, team 임시 service */
    @Transactional(readOnly = true)
    public User findUser(Long uID) {
        return contentRepository.findUser(uID);
    }

    @Transactional(readOnly = true)
    public Team findTeam(Long tID) {
        return contentRepository.findTeam(tID);
    }


    /* content service */
    //content를 upload하는 메서드
    @Transactional
    public Long uploadContent(Long uID, Long tID, ContentInputVO contentInput){

        User user = contentRepository.findUser(uID);
        Team team = contentRepository.findTeam(tID);
        Content content = Content.createContent(user, team, contentInput);
        contentRepository.save(content);

        return content.getCID();
    }

    //cID에 해당하는 content를 리턴하는 메서드
    @Transactional(readOnly = true)
    public Content findOne(Long cID){
        return contentRepository.findOne(cID);
    }

//    public List<Content> findAll(){
//        return contentRepository.findAll();
//    }

    //tID가 동일한 List<Content>를 리턴하는 메서드 -> 메인 페이지용
    @Transactional(readOnly = true)
    public List<Content> findAllByT(Long tID){
        return contentRepository.findAllByT(tID);
    }

    //tID, uID가 동일한 List<Content>를 리턴하는 메서드 -> 개인 페이지용
    @Transactional(readOnly = true)
    public List<Content> findAllByUT(Long uID, Long tID){
        return contentRepository.findAllByUT(uID, tID);
    }

    //수정된 content를 update하기 위해 영속성 content로 만들어줌
    @Transactional
    public void updateContent(Long cID, ContentInputVO contentInput){
        Content content = contentRepository.findOne(cID);
        content.setPhotoRoute(contentInput.getPhotoRoute());
        content.setExplanation(contentInput.getExplanation());
        content.setLocation(contentInput.getLocation());
        content.setWhen(contentInput.getWhen());
    }

    //cID에 해당하는 content를 삭제하는 메서드
    @Transactional
    public void deleteContent(Long cID){
        contentRepository.delete(cID);
    }

    //파일 경로 처리를 위한 메서드
    public String filePath(List<MultipartFile> images) throws Exception{

        String photoRoute = new String();

        if(!CollectionUtils.isEmpty(images)){ //이미지 파일이 존재할 경우
            //프로젝트 내의 static 폴더까지의 절대 경로
            String absolutePath = new File("").getAbsolutePath()+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"static";

            for(MultipartFile image : images){
                String originalFileExtension = new String();
                String contentType = image.getContentType();

                if(ObjectUtils.isEmpty(contentType)){ //확장자가 없는 파일 -> 처리 x
                    break;
                }
                else{
                    if(contentType.contains("image/jpeg"))
                        originalFileExtension = ".jpg";
                    else if(contentType.contains("image/png"))
                        originalFileExtension = ".png";
                    else  // 다른 확장자일 경우 처리 x
                        break;
                }

                String newFileName = System.nanoTime()+originalFileExtension; //이미지 이름이 겹치지 않게 나노시간을 이름으로 사진 저장
                File file = new File(absolutePath+File.separator+"uploadImage"+File.separator+newFileName);
//                System.out.println(System.nanoTime()+" "+originalFileExtension);
//                System.out.println(newFileName);
                image.transferTo(file);
                file.setWritable(true);
                file.setReadable(true);

                if(photoRoute.length() == 0) { // 첫번째 이미지인 경우
                    photoRoute = File.separator + "uploadImage" + File.separator + newFileName;
                }
                else{
                    photoRoute = photoRoute+" "+File.separator + "uploadImage" + File.separator + newFileName;
                }
            }
        }

        return photoRoute;
    }


    public List<String> findTid(LocalDate when, Long tid) {
           return contentRepository.findTid(when, tid);

    }
}
