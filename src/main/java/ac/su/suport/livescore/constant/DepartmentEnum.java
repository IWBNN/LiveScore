package ac.su.suport.livescore.constant;

import java.util.HashMap;
import java.util.Map;

public enum DepartmentEnum {
    THEOLOGY,
    NURSING,
    PHARMACY,
    EARLY_CHILDHOOD_EDUCATION,
    MUSIC,
    ART_AND_DESIGN,
    PHYSICAL_EDUCATION,
    SOCIAL_WELFARE,
    COUNSELING_PSYCHOLOGY,
    ENGLISH_LITERATURE,
    AVIATION_TOURISM_FOREIGN_LANGUAGES,
    GLOBAL_KOREAN_STUDIES,
    BUSINESS_ADMINISTRATION,
    COMPUTER_SCIENCE,
    AI_CONVERGENCE,
    FOOD_NUTRITION,
    HEALTH_MANAGEMENT,
    ENVIRONMENTAL_DESIGN_HORTICULTURE,
    ANIMAL_RESOURCE_SCIENCE,
    CHEMISTRY_LIFE_SCIENCE,
    BIO_CONVERGENCE_ENGINEERING,
    ARCHITECTURE,
    PHYSICAL_THERAPY,
    DATA_CLOUD_ENGINEERING,
    FACULTY_TEAM;

    private static final Map<DepartmentEnum, String> koreanNames = new HashMap<>();

    static {
        koreanNames.put(THEOLOGY, "신학과");
        koreanNames.put(NURSING, "간호학과");
        koreanNames.put(PHARMACY, "약학과");
        koreanNames.put(EARLY_CHILDHOOD_EDUCATION, "유아교육과");
        koreanNames.put(MUSIC, "음악과");
        koreanNames.put(ART_AND_DESIGN, "미술디자인학부");
        koreanNames.put(PHYSICAL_EDUCATION, "체육교육과");
        koreanNames.put(SOCIAL_WELFARE, "사회복지학과");
        koreanNames.put(COUNSELING_PSYCHOLOGY, "상담심리학과");
        koreanNames.put(ENGLISH_LITERATURE, "영어영문학과");
        koreanNames.put(AVIATION_TOURISM_FOREIGN_LANGUAGES, "항공관광외국어학부");
        koreanNames.put(GLOBAL_KOREAN_STUDIES, "글로벌한국학과");
        koreanNames.put(BUSINESS_ADMINISTRATION, "경영학과");
        koreanNames.put(COMPUTER_SCIENCE, "컴퓨터공학부");
        koreanNames.put(AI_CONVERGENCE, "AI융합학부");
        koreanNames.put(FOOD_NUTRITION, "식품영양학과");
        koreanNames.put(HEALTH_MANAGEMENT, "보건관리학과");
        koreanNames.put(ENVIRONMENTAL_DESIGN_HORTICULTURE, "환경디자인원예학과");
        koreanNames.put(ANIMAL_RESOURCE_SCIENCE, "동물생명자원학과");
        koreanNames.put(CHEMISTRY_LIFE_SCIENCE, "화학생명과학과");
        koreanNames.put(BIO_CONVERGENCE_ENGINEERING, "융합바이오공학과");
        koreanNames.put(ARCHITECTURE, "건축학과");
        koreanNames.put(PHYSICAL_THERAPY, "물리치료학과");
        koreanNames.put(DATA_CLOUD_ENGINEERING, "데이터클라우드공학부");
        koreanNames.put(FACULTY_TEAM, "교직원팀");
    }

    public String getKoreanName() {
        return koreanNames.get(this);
    }

    public static DepartmentEnum fromKoreanName(String koreanName) {
        for (Map.Entry<DepartmentEnum, String> entry : koreanNames.entrySet()) {
            if (entry.getValue().equals(koreanName)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("No matching department for Korean name: " + koreanName);
    }
}