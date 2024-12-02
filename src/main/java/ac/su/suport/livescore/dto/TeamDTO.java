package ac.su.suport.livescore.dto;

import ac.su.suport.livescore.constant.DepartmentEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class TeamDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String teamName;
        private DepartmentEnum department;
        private Integer teamPoint;


    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long teamId;
        private String teamName;
        private DepartmentEnum department;
        private Integer teamPoint;
    }

}
