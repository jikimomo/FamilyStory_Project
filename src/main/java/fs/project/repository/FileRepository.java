package fs.project.repository;

import fs.project.domain.File;
import fs.project.domain.Team;
import fs.project.domain.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class FileRepository {

    private final Map<Long, File> store = new HashMap<>();
    private long sequence = 0L; //DB 시퀀스

    public File save(File file){
        file.setId(++sequence);
        store.put(file.getId(),file);
        return file;
    }

    public File findById(Long id){
        return store.get(id);
    }
}
