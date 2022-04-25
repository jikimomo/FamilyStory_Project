package fs.project.service;

import fs.project.repository.TeamRepository;
import fs.project.domain.Team;
import fs.project.domain.TeamEvent;
import fs.project.domain.User;
import fs.project.domain.UserTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;


    // Team
    @Transactional
    public Long saveTeam(Team team){
        System.out.println("saveTeam Service");
        long res = teamRepository.saveTeam(team);
        return res;
    }
    public Team findTeam(Long id){
        System.out.println("findTeam Service");
        Team res = teamRepository.findTeams(id);
        return res;
    }
    // 팀 아이디로 TID를 찾는다.
    public Long findByTeamID(String id){
        System.out.println("findByTeamID Service");
        List<Team> lists = teamRepository.findByTeamID(id);
        return lists.get(0).getTID();
    }
    // 검색페이지에서 검색 키워드를 포함한 팀 아이디 리스트를 가져온다.
    public List<Team> searchTeam(String id){
        System.out.println("searchTeam Service");
        List<Team> res = teamRepository.searchTeam(id);
        return res;
    }

    // User
    @Transactional
    public Long saveUser(User user) {
        System.out.println("saveUser Service");
        Long res = teamRepository.saveUser(user);
        return res;
    }
    public User findUser(Long userUID) {
        System.out.println("findUser Service");
        User res = teamRepository.findUser(userUID);
        return res;
    }
    public List<User> findByUserID(String id){
        System.out.println("findByUserID Service");
        List<User> res = teamRepository.findByUserID(id);
        return res;
    }
    @Transactional
    public void updateMainTeamID(Long uid, Long tid){
        teamRepository.updateMainTeamID(uid,tid);
    }

    @Transactional
    public void updateCurTeamID(Long uid, Long tid){
        teamRepository.updateCurTeamID(uid,tid);
    }

    // Userteam
    @Transactional
    public Long saveUserTeam(UserTeam userTeam){
        System.out.println("saveUserTeam Service");
        Long res = teamRepository.saveUserTeam(userTeam);
        return res;
    }
    public List<UserTeam> findByUID(Long uID) {
        System.out.println("findByUID");
        List<UserTeam> lists = teamRepository.findByUID(uID);
        return lists;
    }

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

    @Transactional
    @Modifying
    public int removeUTID(long utid){
        System.out.println("removeUTID");
        int res = teamRepository.removeUTID(utid);
        return res;
    }


    // 팀이벤트
    @Transactional
    public Long saveTeamEvent(TeamEvent te){
        System.out.println("saveTeamEvent Service");
        Long res = teamRepository.saveTeamEvent(te);
        return res;
    }

    @Transactional
    public List<UserTeam> findUserTeam(String userId, Long tId){
        System.out.println("updateUserTeamID");
        return teamRepository.findUserTeam(userId,tId);
    }


    // === 유효성 체크 === //
    // 유저 아이디 체크
    public int UserIdCheck(String id){
        int res= validateDuplicateUserID(id);
        return res;
    }
    private int validateDuplicateUserID(String id){
        List<User> res = teamRepository.findByUserID(id);
        System.out.println();
        System.out.println("사이즈" + res.size());
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
