package fs.project.service;

import fs.project.repository.TeamRepository;
import fs.project.domain.Team;
import fs.project.domain.TeamEvent;
import fs.project.domain.User;
import fs.project.domain.UserTeam;
import fs.project.repository.TeamRepository2;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamRepository2 teamRepository2;

    // 그룹 검색 + 페이징
    public Page<Team> findByTeamIDContaining(String teamID, Pageable pageable){
        Page<Team> res = teamRepository2.findByTeamIDContaining(teamID, pageable);
        return res;
    }

    // 팀 저장
    @Transactional
    public Long saveTeam(Team team){
        long res = teamRepository.saveTeam(team);
        return res;
    }
    // 팀 찾기
    public Team findTeam(Long id){
        Team res = teamRepository.findTeam(id);
        return res;
    }
    // 팀 아이디로 TID 찾기
    public Long findByTeamID(String id){
        List<Team> lists = teamRepository.findByTeamID(id);
        return lists.get(0).getTID();
    }

    // 유저 찾기
    public User findUser(Long userUID) {
        User res = teamRepository.findUser(userUID);
        return res;
    }
    // 유저 아이디로 유저 찾기
    public User findByUserID(String id){
        List<User> res = teamRepository.findByUserID(id);
        return res.get(0);
    }
    // 유저의 메인 TID 업데이트
    @Transactional
    public void updateMainTID(Long uid, Long tid){
        teamRepository.updateMainTID(uid,tid);
    }
    // 유저의 현재 TID(CurTID) 업데이트
    @Transactional
    public void updateCurTID(Long uid, Long tid){
        teamRepository.updateCurTID(uid,tid);
    }

    // 유저팀 저장
    @Transactional
    public Long saveUserTeam(UserTeam userTeam){
        Long res = teamRepository.saveUserTeam(userTeam);
        return res;
    }
    // UID로 유저팀 찾기
    public List<UserTeam> findByUID(Long uID) {
        List<UserTeam> lists = teamRepository.findByUID(uID);
        return lists;
    }
    // UID & TID로 UTID찾기
    public long findUTID(long uid, long tid){
        // 유저가 가입한 팀 리스트
        List<UserTeam> list = teamRepository.findByUID(uid);
        // 전달받은 tid로 utid를 가져온다.
        long utid=0;
        for(UserTeam ut : list){
            if(ut.getTeam().getTID() == tid){
                utid=ut.getUtID();
            }
        }
        return utid;
    }
    // UTID로 유저팀 삭제
    @Transactional
    @Modifying
    public int removeUTID(long utid){
        int res = teamRepository.removeUTID(utid);
        return res;
    }

    // 팀이벤트 저장
    @Transactional
    public Long saveTeamEvent(TeamEvent te){
        Long res = teamRepository.saveTeamEvent(te);
        return res;
    }
    // UTID로 유저팀 찾기
    @Transactional
    public UserTeam findUserTeam(Long utid){
        return teamRepository.findUserTeam(utid);
    }

    // === 유효성 체크 === //
    // 유저 아이디 체크
    public int UserIdCheck(String id){
        int res= validateDuplicateUserID(id);
        return res;
    }
    private int validateDuplicateUserID(String id){
        List<User> res = teamRepository.findByUserID(id);
        return res.size();
    }

    // 팀아이디 체크
    public int TeamIdCheck(String id){
        int res= validateDuplicateTeamID(id);
        return res;
    }
    private int validateDuplicateTeamID(String id){
        List<Team> res = teamRepository.findByTeamID(id);
        return res.size();
    }

    // 유저-팀아이디 중복 체크
    public int UserTeamIdCheck(long uid, long tid){
        int res= validateDuplicateUserTeamID(uid,tid);
        return res;
    }
    private int validateDuplicateUserTeamID(long uid, long tid){
        List<UserTeam> list = teamRepository.findByUID(uid);
        // 입력받은 tid가 존재하면 이미 가입 요청 및 그룹원인 상태.
        int cnt=0;
        for(UserTeam ut : list){
            if(ut.getTeam().getTID() == tid){
                cnt++;
            }
        }
        return cnt;
    }

}
