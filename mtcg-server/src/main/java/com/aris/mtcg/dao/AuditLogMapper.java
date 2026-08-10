package com.aris.mtcg.dao;

import com.aris.mtcg.domain.entity.AuditLogDO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审计日志 DAO
 *
 * @author pengYuJun
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLogDO> {}
