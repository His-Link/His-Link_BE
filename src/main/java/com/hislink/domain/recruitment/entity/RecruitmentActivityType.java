package com.hislink.domain.recruitment.entity;

import com.hislink.common.exception.BusinessException;
import com.hislink.common.exception.ErrorCode;

public enum RecruitmentActivityType {
    PROJECT,
    HACKATHON,
    CONTEST,
    COMPETITION;

    /** 목록 필터용: 빈 값이면 null (전체) */
    public static RecruitmentActivityType parseFilter(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return valueOf(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "유효하지 않은 활동 유형 필터입니다.");
        }
    }

    /** 작성·수정용 */
    public static RecruitmentActivityType parseRequired(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "활동 유형을 선택해 주세요.");
        }
        try {
            return valueOf(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "유효하지 않은 활동 유형입니다.");
        }
    }

    /** DB 조회용: 빈·잘못된 값은 PROJECT */
    public static RecruitmentActivityType fromDbValue(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return PROJECT;
        }
        try {
            return valueOf(dbData.trim());
        } catch (IllegalArgumentException ex) {
            return PROJECT;
        }
    }
}
