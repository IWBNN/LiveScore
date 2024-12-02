package ac.su.suport.livescore.service;

import ac.su.suport.livescore.constant.DepartmentEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DepartmentService {

    public List<DepartmentEnum> getAllDepartments() {
        return Arrays.asList(DepartmentEnum.values());
    }
}
