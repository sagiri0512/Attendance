package com.sagiri.mapper;

import com.sagiri.entity.SocialInsuranceConfig;

/**
 * 社保公积金配置 Mapper
 *
 * <p>操作 social_insurance_config 表。</p>
 *
 * @author sagiri
 */
public interface SocialInsuranceConfigMapper {

    /**
     * 插入社保公积金配置
     *
     * @param config 社保配置实体
     * @return 影响行数
     */
    Integer insert(SocialInsuranceConfig config);
}
