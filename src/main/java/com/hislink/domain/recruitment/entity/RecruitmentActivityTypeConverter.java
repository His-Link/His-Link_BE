package com.hislink.domain.recruitment.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * DB에 빈 문자열·잘못된 값이 있어도 조회 시 PROJECT로 보정합니다.
 */
@Converter
public class RecruitmentActivityTypeConverter implements AttributeConverter<RecruitmentActivityType, String> {

    @Override
    public String convertToDatabaseColumn(RecruitmentActivityType attribute) {
        if (attribute == null) {
            return RecruitmentActivityType.PROJECT.name();
        }
        return attribute.name();
    }

    @Override
    public RecruitmentActivityType convertToEntityAttribute(String dbData) {
        return RecruitmentActivityType.fromDbValue(dbData);
    }
}
