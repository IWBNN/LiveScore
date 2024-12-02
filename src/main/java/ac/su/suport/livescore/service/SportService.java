package ac.su.suport.livescore.service;

import ac.su.suport.livescore.constant.SportEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SportService {

    public List<SportEnum> getAllSports() {
        return Arrays.asList(SportEnum.values());
    }
}
