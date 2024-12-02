package ac.su.suport.livescore.repository;

import ac.su.suport.livescore.constant.DepartmentEnum;
import ac.su.suport.livescore.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
        Optional<Team> findByDepartment(DepartmentEnum department);

}